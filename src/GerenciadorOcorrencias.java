
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class GerenciadorOcorrencias implements AutoCloseable {

    private final Connection conexao;

    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");

    private static final String CONSULTA = """
            SELECT o.*, m.nome AS nome_municipio
            FROM ocorrencia o
            JOIN municipio m
                ON m.id_municipio = o.id_municipio
            """;

    // CONECTAR AO POSTGRESQL
    public GerenciadorOcorrencias() {

        JPasswordField campo = new JPasswordField();

        int escolha = JOptionPane.showConfirmDialog(
                null,
                campo,
                "Senha do PostgreSQL",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (escolha != JOptionPane.OK_OPTION) {
            throw new IllegalStateException(
                    "Conexão cancelada."
            );
        }

        try {
            conexao = Conexao.conectar(
                    new String(campo.getPassword())
            );

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Erro de conexão: " + e.getMessage(), e
            );

        } finally {
            java.util.Arrays.fill(
                    campo.getPassword(), '\0'
            );
        }
    }

    // PREENCHER OS PARAMETROS SQL
    private void preencher(
            PreparedStatement p,
            Ocorrencia o) throws SQLException {

        if (o == null) {
            throw new IllegalArgumentException(
                    "Ocorrência não informada."
            );
        }

        String tipo;

        if (o instanceof QueimadaRural) {
            tipo = "rural";

        } else if (o instanceof QueimadaUrbana) {
            tipo = "urbana";

        } else {
            throw new IllegalArgumentException(
                    "Tipo de ocorrência inválido."
            );
        }

        if (o.getDetalhe() == null ||
                o.getDetalhe().isBlank()) {

            throw new IllegalArgumentException(
                    "Informe o bairro ou localidade."
            );
        }

        LocalDate data = LocalDate.parse(
                o.getData(), DATA
        );

        p.setDate(1, Date.valueOf(data));
        p.setDouble(2, o.getTemperatura());
        p.setDouble(3, o.getUmidade());
        p.setString(4, o.getDescricao());
        p.setString(5, o.getSituacao());
        p.setInt(6, o.getIdMunicipio());
        p.setInt(7, o.getIdAgente());

        // CORRECAO DO PARAMETRO 8
        p.setString(8, tipo);

        // VEGETACAO NAO E MAIS UTILIZADA
        p.setNull(9, Types.VARCHAR);

        // BAIRRO/LOCALIDADE PARA RURAL E URBANA
        p.setString(10, o.getDetalhe().trim());
    }

    // CADASTRAR OCORRENCIA
    public boolean cadastrar(Ocorrencia o) {

        if (o == null) {
            return false;
        }

        String sql = """
                INSERT INTO ocorrencia
                (
                    data,
                    temperatura,
                    umidade,
                    descricao,
                    situacao,
                    id_municipio,
                    id_agente,
                    tipo_ocorrencia,
                    tipo_vegetacao,
                    bairro
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id_ocorrencia
                """;

        try (PreparedStatement p =
                     conexao.prepareStatement(sql)) {

            preencher(p, o);

            try (ResultSet r = p.executeQuery()) {

                if (r.next()) {
                    o.setId(r.getInt(1));
                    return true;
                }
            }

            return false;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Erro ao cadastrar: " + e.getMessage(), e
            );
        }
    }

    // CONVERTER DADOS DO BANCO EM OBJETOS JAVA
    private Ocorrencia converter(
            ResultSet r) throws SQLException {

        int id = r.getInt("id_ocorrencia");

        int idMunicipio =
                r.getInt("id_municipio");

        String municipio =
                r.getString("nome_municipio");

        int idAgente =
                r.getInt("id_agente");

        String data = r.getDate("data")
                .toLocalDate()
                .format(DATA);

        double temperatura =
                r.getDouble("temperatura");

        double umidade =
                r.getDouble("umidade");

        String descricao =
                r.getString("descricao");

        String situacao =
                r.getString("situacao");

        String localidade =
                r.getString("bairro");

        String tipo =
                r.getString("tipo_ocorrencia");

        if ("rural".equals(tipo)) {

            return new QueimadaRural(
                    id,
                    idMunicipio,
                    municipio,
                    idAgente,
                    data,
                    temperatura,
                    umidade,
                    descricao,
                    situacao,
                    localidade
            );
        }

        if ("urbana".equals(tipo)) {

            return new QueimadaUrbana(
                    id,
                    idMunicipio,
                    municipio,
                    idAgente,
                    data,
                    temperatura,
                    umidade,
                    descricao,
                    situacao,
                    localidade
            );
        }

        throw new IllegalStateException(
                "Tipo inválido no banco para ocorrência "
                        + id
        );
    }

    // LISTAR OCORRENCIAS
    public ArrayList<Ocorrencia> listar() {

        ArrayList<Ocorrencia> lista =
                new ArrayList<>();

        String sql = CONSULTA
                + " ORDER BY o.id_ocorrencia";

        try (PreparedStatement p =
                     conexao.prepareStatement(sql);

             ResultSet r = p.executeQuery()) {

            while (r.next()) {
                lista.add(converter(r));
            }

            return lista;

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Erro ao listar: " + e.getMessage(), e
            );
        }
    }

    // BUSCAR OCORRENCIA PELO ID
    public Ocorrencia buscarPorId(int id) {

        String sql = CONSULTA
                + " WHERE o.id_ocorrencia = ?";

        try (PreparedStatement p =
                     conexao.prepareStatement(sql)) {

            p.setInt(1, id);

            try (ResultSet r = p.executeQuery()) {

                return r.next()
                        ? converter(r)
                        : null;
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Erro na busca: " + e.getMessage(), e
            );
        }
    }

    // EDITAR OCORRENCIA
    public boolean alterar(
            int id, Ocorrencia nova) {

        if (nova == null || nova.getId() != id) {
            return false;
        }

        String sql = """
                UPDATE ocorrencia
                SET
                    data = ?,
                    temperatura = ?,
                    umidade = ?,
                    descricao = ?,
                    situacao = ?,
                    id_municipio = ?,
                    id_agente = ?,
                    tipo_ocorrencia = ?,
                    tipo_vegetacao = ?,
                    bairro = ?
                WHERE id_ocorrencia = ?
                """;

        try (PreparedStatement p =
                     conexao.prepareStatement(sql)) {

            preencher(p, nova);

            p.setInt(11, id);

            return p.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Erro ao alterar: " + e.getMessage(), e
            );
        }
    }

    // EXCLUIR OCORRENCIA
    public boolean excluir(int id) {

        try {
            boolean modoAnterior =
                    conexao.getAutoCommit();

            conexao.setAutoCommit(false);

            try {

                // EXCLUIR ATENDIMENTOS ASSOCIADOS
                String sqlAtendimento = """
                        DELETE FROM atendimento
                        WHERE id_ocorrencia = ?
                        """;

                try (PreparedStatement p =
                             conexao.prepareStatement(
                                     sqlAtendimento
                             )) {

                    p.setInt(1, id);
                    p.executeUpdate();
                }

                // EXCLUIR A OCORRENCIA
                String sqlOcorrencia = """
                        DELETE FROM ocorrencia
                        WHERE id_ocorrencia = ?
                        """;

                int linhas;

                try (PreparedStatement p =
                             conexao.prepareStatement(
                                     sqlOcorrencia
                             )) {

                    p.setInt(1, id);
                    linhas = p.executeUpdate();
                }

                if (linhas > 0) {
                    conexao.commit();
                } else {
                    conexao.rollback();
                }

                return linhas > 0;

            } catch (SQLException e) {

                conexao.rollback();
                throw e;

            } finally {

                conexao.setAutoCommit(modoAnterior);
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Erro ao excluir: " + e.getMessage(), e
            );
        }
    }

    // LISTAR OPCOES PARA AS CAIXAS DE SELECAO
    private ArrayList<String[]> listarOpcoes(
            String sql) {

        ArrayList<String[]> lista =
                new ArrayList<>();

        try (PreparedStatement p =
                     conexao.prepareStatement(sql);

             ResultSet r = p.executeQuery()) {

            while (r.next()) {

                lista.add(new String[]{
                        r.getString(1),
                        r.getString(2)
                });
            }

            return lista;

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Erro ao consultar opções: "
                            + e.getMessage(), e
            );
        }
    }

    // LISTAR MUNICIPIOS
    public ArrayList<String[]> listarMunicipios() {

        return listarOpcoes("""
                SELECT id_municipio, nome
                FROM municipio
                ORDER BY nome
                """);
    }

    // LISTAR EQUIPES
    public ArrayList<String[]> listarEquipes() {

        return listarOpcoes("""
                SELECT id_equipe, nome
                FROM equipe
                ORDER BY nome
                """);
    }

    // LISTAR AGENTES COM CPF PARCIALMENTE OCULTO
    public ArrayList<String[]> listarAgentes() {

        return listarOpcoes("""
                SELECT
                    id_agente,
                    nome || ' - CPF final ' || RIGHT(cpf, 2)
                FROM agente
                ORDER BY nome
                """);
    }

    // CADASTRAR MUNICIPIO
    public int cadastrarMunicipio(String nome) {

        if (nome == null ||
                nome.isBlank() ||
                nome.trim().length() > 100) {

            throw new IllegalArgumentException(
                    "Informe um município válido."
            );
        }

        nome = nome.trim();

        // VERIFICAR SE JA EXISTE
        String consulta = """
                SELECT id_municipio
                FROM municipio
                WHERE lower(btrim(nome)) = lower(?)
                  AND estado = 'MA'
                ORDER BY id_municipio
                LIMIT 1
                """;

        try (PreparedStatement p =
                     conexao.prepareStatement(consulta)) {

            p.setString(1, nome);

            try (ResultSet r = p.executeQuery()) {

                if (r.next()) {
                    return r.getInt(1);
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Erro ao buscar município: "
                            + e.getMessage(), e
            );
        }

        // CADASTRAR NOVO MUNICIPIO
        String sql = """
                INSERT INTO municipio (nome, estado)
                VALUES (?, 'MA')
                RETURNING id_municipio
                """;

        try (PreparedStatement p =
                     conexao.prepareStatement(sql)) {

            p.setString(1, nome);

            try (ResultSet r = p.executeQuery()) {

                if (r.next()) {
                    return r.getInt(1);
                }
            }

            throw new IllegalStateException(
                    "Município não foi incluído."
            );

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Erro ao cadastrar município: "
                            + e.getMessage(), e
            );
        }
    }

    // VALIDAR CPF
    private static boolean cpfValido(String cpf) {

        if (!cpf.matches("[0-9]{11}") ||
                cpf.chars().distinct().count() == 1) {

            return false;
        }

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * (10 - i);
        }

        int d1 = (soma * 10) % 11;

        if (d1 == 10) {
            d1 = 0;
        }

        if (d1 != cpf.charAt(9) - '0') {
            return false;
        }

        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * (11 - i);
        }

        int d2 = (soma * 10) % 11;

        if (d2 == 10) {
            d2 = 0;
        }

        return d2 == cpf.charAt(10) - '0';
    }

    // CADASTRAR AGENTE USANDO CPF
    public int cadastrarAgente(
            String nome,
            String cpfDigitado,
            int idEquipe) {

        if (nome == null ||
                nome.isBlank() ||
                nome.trim().length() > 100) {

            throw new IllegalArgumentException(
                    "Nome do agente inválido."
            );
        }

        if (cpfDigitado == null) {
            throw new IllegalArgumentException(
                    "Informe o CPF."
            );
        }

        // ACEITAR CPF COM OU SEM PONTUACAO
        String cpf = cpfDigitado.replaceAll(
                "[^0-9]", ""
        );

        if (!cpfValido(cpf)) {

            throw new IllegalArgumentException(
                    "CPF inválido."
            );
        }

        if (idEquipe <= 0) {

            throw new IllegalArgumentException(
                    "Selecione uma equipe."
            );
        }

        String sql = """
                INSERT INTO agente
                (
                    nome,
                    cpf,
                    id_equipe
                )
                VALUES (?, ?, ?)
                RETURNING id_agente
                """;

        try (PreparedStatement p =
                     conexao.prepareStatement(sql)) {

            p.setString(1, nome.trim());
            p.setString(2, cpf);
            p.setInt(3, idEquipe);

            try (ResultSet r = p.executeQuery()) {

                if (r.next()) {
                    return r.getInt(1);
                }
            }

            throw new IllegalStateException(
                    "Agente não foi incluído."
            );

        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {

                throw new IllegalArgumentException(
                        "CPF já cadastrado.", e
                );
            }

            throw new IllegalStateException(
                    "Erro ao cadastrar agente: "
                            + e.getMessage(), e
            );
        }
    }

    // ENCERRAR A CONEXAO
    @Override
    public void close() {

        try {

            if (!conexao.isClosed()) {
                conexao.close();
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Erro ao fechar conexão: "
                            + e.getMessage(), e
            );
        }
    }
}
