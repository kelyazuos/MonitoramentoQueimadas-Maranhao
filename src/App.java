
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;

public class App {

    private static GerenciadorOcorrencias gerenciador;

    private static final Color FUNDO =
            new Color(18, 18, 18);

    private static final Color LARANJA =
            new Color(235, 118, 28);

    private static final DateTimeFormatter DATA =
            DateTimeFormatter
                    .ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    // OPCOES DE MUNICIPIOS, AGENTES E EQUIPES
    private static class Opcao {

        final int id;
        final String nome;

        Opcao(int id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        @Override
        public String toString() {
            return nome;
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                gerenciador =
                        new GerenciadorOcorrencias();

                abrirJanela();

            } catch (RuntimeException e) {

                JOptionPane.showMessageDialog(
                        null,
                        e.getMessage(),
                        "Erro ao iniciar",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    // EXIBIR MENSAGENS DE ERRO
    private static void executarSeguro(
            Component origem,
            Runnable acao) {

        try {
            acao.run();

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    origem,
                    e.getMessage(),
                    "Não foi possível concluir",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // CRIAR BOTOES
    private static JButton botao(String texto) {

        JButton b = new JButton(texto);

        b.setBackground(LARANJA);
        b.setForeground(Color.BLACK);

        b.setFont(
                new Font("Arial", Font.BOLD, 17)
        );

        b.setFocusPainted(false);
        b.setOpaque(true);

        return b;
    }

    // TELA INICIAL
    private static void abrirJanela() {

        JFrame janela = new JFrame(
                "Sistema de Monitoramento de Queimadas no Maranhão"
        );

        janela.setSize(850, 580);

        janela.setMinimumSize(
                new Dimension(670, 490)
        );

        janela.setLocationRelativeTo(null);

        janela.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        janela.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                if (gerenciador != null) {
                    gerenciador.close();
                }
            }
        });

        JPanel tela = new JPanel(
                new BorderLayout(15, 25)
        );

        tela.setBackground(FUNDO);

        tela.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 45, 30, 45
                )
        );

        JLabel titulo = new JLabel(
                "MONITORAMENTO DE QUEIMADAS",
                SwingConstants.CENTER
        );

        titulo.setForeground(LARANJA);

        titulo.setFont(
                new Font("Arial", Font.BOLD, 27)
        );

        JLabel subtitulo = new JLabel(
                "MARANHÃO | Java Swing + PostgreSQL",
                SwingConstants.CENTER
        );

        subtitulo.setForeground(Color.WHITE);

        JPanel cabecalho = new JPanel(
                new GridLayout(2, 1, 0, 10)
        );

        cabecalho.setOpaque(false);

        cabecalho.add(titulo);
        cabecalho.add(subtitulo);

        tela.add(
                cabecalho,
                BorderLayout.NORTH
        );

        JPanel botoes = new JPanel(
                new GridLayout(4, 1, 0, 16)
        );

        botoes.setOpaque(false);

        botoes.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 115, 15, 115
                )
        );

        JButton cadastrar =
                botao("Cadastrar Ocorrência");

        JButton consultar =
                botao("Consultar Ocorrências");

        JButton editar =
                botao("Editar Ocorrência");

        JButton excluir =
                botao("Excluir Ocorrência");

        botoes.add(cadastrar);
        botoes.add(consultar);
        botoes.add(editar);
        botoes.add(excluir);

        cadastrar.addActionListener(e ->
                executarSeguro(
                        janela,
                        () -> cadastrar(janela)
                )
        );

        consultar.addActionListener(e ->
                executarSeguro(
                        janela,
                        () -> consultar(janela)
                )
        );

        editar.addActionListener(e ->
                executarSeguro(
                        janela,
                        () -> editar(janela)
                )
        );

        excluir.addActionListener(e ->
                executarSeguro(
                        janela,
                        () -> excluir(janela)
                )
        );

        tela.add(
                botoes,
                BorderLayout.CENTER
        );

        janela.setContentPane(tela);
        janela.setVisible(true);
    }

    // COMBOBOX COM REGISTROS DO BANCO
    private static JComboBox<Opcao> combo(
            ArrayList<String[]> dados) {

        JComboBox<Opcao> caixa =
                new JComboBox<>();

        for (String[] item : dados) {

            caixa.addItem(
                    new Opcao(
                            Integer.parseInt(item[0]),
                            item[1]
                    )
            );
        }

        return caixa;
    }

    // SELECIONAR ITEM PELO ID
    private static void selecionar(
            JComboBox<Opcao> caixa,
            int id) {

        for (int i = 0;
             i < caixa.getItemCount();
             i++) {

            if (caixa.getItemAt(i).id == id) {

                caixa.setSelectedIndex(i);
                return;
            }
        }

        throw new IllegalArgumentException(
                "Registro " + id
                        + " não encontrado."
        );
    }

    // PAINEL COM SELECAO E BOTAO NOVO
    private static JPanel comBotao(
            JComboBox<Opcao> combo,
            JButton novo) {

        JPanel p = new JPanel(
                new BorderLayout(8, 0)
        );

        p.add(
                combo,
                BorderLayout.CENTER
        );

        p.add(
                novo,
                BorderLayout.EAST
        );

        return p;
    }

    // CADASTRAR NOVO MUNICIPIO
    private static void novoMunicipio(
            JFrame janela,
            JComboBox<Opcao> municipios) {

        String nome = JOptionPane.showInputDialog(
                janela,
                "Nome do município do Maranhão:",
                "Novo Município",
                JOptionPane.PLAIN_MESSAGE
        );

        if (nome == null) {
            return;
        }

        executarSeguro(janela, () -> {

            int id =
                    gerenciador.cadastrarMunicipio(nome);

            municipios.removeAllItems();

            for (String[] item :
                    gerenciador.listarMunicipios()) {

                municipios.addItem(
                        new Opcao(
                                Integer.parseInt(item[0]),
                                item[1]
                        )
                );
            }

            selecionar(municipios, id);

            JOptionPane.showMessageDialog(
                    janela,
                    "Município disponível para seleção!"
            );
        });
    }

    // CADASTRAR NOVO AGENTE
    private static void novoAgente(
            JFrame janela,
            JComboBox<Opcao> agentes) {

        executarSeguro(janela, () -> {

            JComboBox<Opcao> equipes = combo(
                    gerenciador.listarEquipes()
            );

            if (equipes.getItemCount() == 0) {

                JOptionPane.showMessageDialog(
                        janela,
                        "Nenhuma equipe cadastrada."
                );

                return;
            }

            JTextField nome =
                    new JTextField(20);

            JTextField cpf =
                    new JTextField(16);

            JPanel p = new JPanel(
                    new GridLayout(0, 2, 10, 10)
            );

            p.add(new JLabel("Nome:"));
            p.add(nome);

            p.add(new JLabel("CPF (11 dígitos):"));
            p.add(cpf);

            p.add(new JLabel("Equipe:"));
            p.add(equipes);

            int resposta =
                    JOptionPane.showConfirmDialog(
                            janela,
                            p,
                            "Novo Agente",
                            JOptionPane.OK_CANCEL_OPTION
                    );

            if (resposta !=
                    JOptionPane.OK_OPTION) {

                return;
            }

            Opcao equipe =
                    (Opcao) equipes.getSelectedItem();

            if (equipe == null) {

                throw new IllegalArgumentException(
                        "Selecione a equipe."
                );
            }

            int id = gerenciador.cadastrarAgente(
                    nome.getText(),
                    cpf.getText(),
                    equipe.id
            );

            agentes.removeAllItems();

            for (String[] item :
                    gerenciador.listarAgentes()) {

                agentes.addItem(
                        new Opcao(
                                Integer.parseInt(item[0]),
                                item[1]
                        )
                );
            }

            selecionar(agentes, id);

            JOptionPane.showMessageDialog(
                    janela,
                    "Agente cadastrado com sucesso!"
            );
        });
    }

    // ADICIONAR CAMPO AO FORMULARIO
    private static void campo(
            JPanel formulario,
            String titulo,
            JComponent componente) {

        JLabel label = new JLabel(titulo);

        label.setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        formulario.add(label);
        formulario.add(componente);
    }

    // FORMULARIO DE CADASTRO E EDICAO
    private static Ocorrencia formulario(
            JFrame janela,
            Ocorrencia atual) {

        JComboBox<Opcao> municipios = combo(
                gerenciador.listarMunicipios()
        );

        JComboBox<Opcao> agentes = combo(
                gerenciador.listarAgentes()
        );

        JButton btnMunicipio =
                new JButton("+ Novo");

        JButton btnAgente =
                new JButton("+ Novo");

        btnMunicipio.addActionListener(e ->
                novoMunicipio(
                        janela,
                        municipios
                )
        );

        btnAgente.addActionListener(e ->
                novoAgente(
                        janela,
                        agentes
                )
        );

        JTextField data = new JTextField(
                atual == null
                        ? LocalDate.now().format(DATA)
                        : atual.getData()
        );

        JTextField temperatura = new JTextField(
                atual == null
                        ? ""
                        : String.valueOf(
                        atual.getTemperatura()
                )
        );

        JTextField umidade = new JTextField(
                atual == null
                        ? ""
                        : String.valueOf(
                        atual.getUmidade()
                )
        );

        JTextArea descricao = new JTextArea(
                atual == null
                        ? ""
                        : atual.getDescricao(),
                3,
                23
        );

        descricao.setLineWrap(true);
        descricao.setWrapStyleWord(true);

        JComboBox<String> situacao =
                new JComboBox<>(
                        new String[]{
                                "registrada",
                                "em_atendimento",
                                "controlada",
                                "encerrada"
                        }
                );

        JComboBox<String> tipo =
                new JComboBox<>(
                        new String[]{
                                "Rural",
                                "Urbana"
                        }
                );

        // BAIRRO OU LOCALIDADE PARA OS DOIS TIPOS
        JTextField localidade = new JTextField(
                atual == null
                        ? ""
                        : atual.getDetalhe()
        );

        // PREENCHER DADOS AO EDITAR
        if (atual != null) {

            selecionar(
                    municipios,
                    atual.getIdMunicipio()
            );

            selecionar(
                    agentes,
                    atual.getIdAgente()
            );

            situacao.setSelectedItem(
                    atual.getSituacao()
            );

            tipo.setSelectedItem(
                    atual instanceof QueimadaUrbana
                            ? "Urbana"
                            : "Rural"
            );
        }

        JPanel p = new JPanel(
                new GridLayout(0, 2, 10, 12)
        );

        p.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );

        campo(
                p,
                "Município:",
                comBotao(
                        municipios,
                        btnMunicipio
                )
        );

        campo(
                p,
                "Agente responsável:",
                comBotao(
                        agentes,
                        btnAgente
                )
        );

        campo(
                p,
                "Data (DD/MM/AAAA):",
                data
        );

        campo(
                p,
                "Temperatura (°C):",
                temperatura
        );

        campo(
                p,
                "Umidade (%):",
                umidade
        );

        campo(
                p,
                "Descrição:",
                new JScrollPane(descricao)
        );

        campo(
                p,
                "Situação:",
                situacao
        );

        campo(
                p,
                "Tipo de ocorrência:",
                tipo
        );

        campo(
                p,
                "Bairro / localidade:",
                localidade
        );

        JScrollPane rolagem = new JScrollPane(
                p,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        rolagem.setPreferredSize(
                new Dimension(670, 490)
        );

        // VALIDACAO DOS CAMPOS
        while (true) {

            int resposta =
                    JOptionPane.showConfirmDialog(
                            janela,
                            rolagem,
                            atual == null
                                    ? "Cadastrar Ocorrência"
                                    : "Editar Ocorrência #"
                                    + atual.getId(),
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );

            if (resposta !=
                    JOptionPane.OK_OPTION) {

                return null;
            }

            try {

                Opcao municipio =
                        (Opcao) municipios.getSelectedItem();

                Opcao agente =
                        (Opcao) agentes.getSelectedItem();

                if (municipio == null ||
                        agente == null) {

                    throw new IllegalArgumentException(
                            "Selecione município e agente."
                    );
                }

                String dataTexto =
                        data.getText().trim();

                LocalDate.parse(
                        dataTexto,
                        DATA
                );

                double temp = numero(
                        temperatura.getText(),
                        "temperatura"
                );

                double umi = numero(
                        umidade.getText(),
                        "umidade"
                );

                if (temp < -999.99 ||
                        temp > 999.99) {

                    throw new IllegalArgumentException(
                            "Temperatura fora do intervalo permitido."
                    );
                }

                if (umi < 0 || umi > 100) {

                    throw new IllegalArgumentException(
                            "Umidade deve ficar entre 0 e 100%."
                    );
                }

                String desc =
                        descricao.getText().trim();

                String bairro =
                        localidade.getText().trim();

                if (desc.isBlank()) {

                    throw new IllegalArgumentException(
                            "Informe a descrição."
                    );
                }

                if (bairro.isBlank() ||
                        bairro.length() > 100) {

                    throw new IllegalArgumentException(
                            "Informe bairro/localidade "
                                    + "(até 100 caracteres)."
                    );
                }

                int id = atual == null
                        ? 0
                        : atual.getId();

                String estado =
                        (String) situacao.getSelectedItem();

                // OCORRENCIA URBANA
                if ("Urbana".equals(
                        tipo.getSelectedItem())) {

                    return new QueimadaUrbana(
                            id,
                            municipio.id,
                            municipio.nome,
                            agente.id,
                            dataTexto,
                            temp,
                            umi,
                            desc,
                            estado,
                            bairro
                    );
                }

                // OCORRENCIA RURAL
                return new QueimadaRural(
                        id,
                        municipio.id,
                        municipio.nome,
                        agente.id,
                        dataTexto,
                        temp,
                        umi,
                        desc,
                        estado,
                        bairro
                );

            } catch (RuntimeException erro) {

                JOptionPane.showMessageDialog(
                        janela,
                        erro.getMessage(),
                        "Dados inválidos",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }

    // CONVERTER VALORES NUMERICOS
    private static double numero(
            String texto,
            String campo) {

        try {

            double valor = Double.parseDouble(
                    texto.trim().replace(',', '.')
            );

            if (!Double.isFinite(valor)) {
                throw new NumberFormatException();
            }

            return valor;

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Número inválido para "
                            + campo + "."
            );
        }
    }

    // CADASTRAR OCORRENCIA
    private static void cadastrar(JFrame janela) {

        Ocorrencia nova =
                formulario(janela, null);

        if (nova != null &&
                gerenciador.cadastrar(nova)) {

            JOptionPane.showMessageDialog(
                    janela,
                    "Ocorrência #" + nova.getId()
                            + " cadastrada!\n"
                            + "Índice de Risco: "
                            + String.format(
                            "%.2f",
                            nova.calcularRisco()
                    )
            );
        }
    }


    // CONSULTAR OCORRENCIAS COM DETALHES
    private static void consultar(JFrame janela) {

        ArrayList<Ocorrencia> registros =
                gerenciador.listar();

        if (registros.isEmpty()) {
            JOptionPane.showMessageDialog(
                    janela,
                    "Nenhuma ocorrência cadastrada."
            );
            return;
        }

        // ASSOCIAR IDs AOS NOMES DOS AGENTES
        java.util.Map<Integer, String> nomes =
                new java.util.HashMap<>();

        for (String[] agente :
                gerenciador.listarAgentes()) {

            int id = Integer.parseInt(agente[0]);
            String nome = agente[1];

            // NAO EXIBIR CPF NA TABELA
            int posicao = nome.indexOf(
                    " - CPF final "
            );

            if (posicao >= 0) {
                nome = nome.substring(0, posicao);
            }

            nomes.put(id, nome);
        }

        // COLUNAS PRINCIPAIS
        String[] colunas = {
                "ID",
                "Município",
                "Data",
                "Tipo",
                "Agente responsável",
                "Situação",
                "Bairro / localidade"
        };

        DefaultTableModel modelo =
                new DefaultTableModel(colunas, 0) {

                    @Override
                    public boolean isCellEditable(
                            int linha, int coluna) {
                        return false;
                    }
                };

        java.util.Map<Integer, Ocorrencia> indice =
                new java.util.HashMap<>();

        for (Ocorrencia o : registros) {

            indice.put(o.getId(), o);

            modelo.addRow(new Object[]{
                    o.getId(),
                    o.getMunicipio(),
                    o.getData(),

                    o instanceof QueimadaUrbana
                            ? "Urbana" : "Rural",

                    nomes.getOrDefault(
                            o.getIdAgente(),
                            "Agente #" + o.getIdAgente()
                    ),

                    o.getSituacao(),
                    o.getDetalhe()
            });
        }

        // CRIAR TABELA
        JTable tabela = new JTable(modelo);

        tabela.setAutoCreateRowSorter(true);
        tabela.setRowHeight(27);

        tabela.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabela.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        // DEFINIR LARGURA DAS COLUNAS
        int[] larguras = {
                55, 150, 110, 90, 180, 140, 230
        };

        for (int i = 0; i < larguras.length; i++) {

            tabela.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(larguras[i]);
        }

        // AREA PARA OS DETALHES COMPLETOS
        JTextArea detalhes = new JTextArea(9, 65);

        detalhes.setEditable(false);
        detalhes.setLineWrap(true);
        detalhes.setWrapStyleWord(true);

        detalhes.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        detalhes.setMargin(
                new Insets(9, 9, 9, 9)
        );

        // MOSTRAR DETALHES AO SELECIONAR UMA LINHA
        tabela.getSelectionModel()
                .addListSelectionListener(evento -> {

                    if (evento.getValueIsAdjusting()
                            || tabela.getSelectedRow() < 0) {
                        return;
                    }

                    int linha = tabela.convertRowIndexToModel(
                            tabela.getSelectedRow()
                    );

                    int id = (Integer) modelo.getValueAt(
                            linha, 0
                    );

                    Ocorrencia o = indice.get(id);

                    if (o == null) {
                        return;
                    }

                    String agente = nomes.getOrDefault(
                            o.getIdAgente(),
                            "Agente #" + o.getIdAgente()
                    );

                    detalhes.setText(
                            "Ocorrência #" + o.getId()
                                    + " | "
                                    + (o instanceof QueimadaUrbana
                                    ? "Urbana" : "Rural")
                                    + "\n"

                                    + "Município: "
                                    + o.getMunicipio()

                                    + "    Bairro/localidade: "
                                    + o.getDetalhe()
                                    + "\n"

                                    + "Agente responsável: "
                                    + agente
                                    + "\n"

                                    + "Data: " + o.getData()

                                    + "    Situação: "
                                    + o.getSituacao()
                                    + "\n"

                                    + "Temperatura: "
                                    + o.getTemperatura()
                                    + " °C"

                                    + "    Umidade: "
                                    + o.getUmidade()
                                    + "%\n"

                                    + "Índice de Risco: "
                                    + String.format(
                                    "%.2f", o.calcularRisco()
                            )
                                    + "\n\n"

                                    + "DESCRIÇÃO COMPLETA:\n"
                                    + o.getDescricao()
                    );

                    detalhes.setCaretPosition(0);
                });

        // ORGANIZAR TABELA E AREA DE DETALHES
        JPanel painel = new JPanel(
                new BorderLayout(8, 8)
        );

        painel.add(
                new JLabel(
                        "Selecione uma ocorrência para ver os detalhes abaixo:"
                ),
                BorderLayout.NORTH
        );

        JScrollPane tabelaRolagem =
                new JScrollPane(tabela);

        tabelaRolagem.setPreferredSize(
                new Dimension(990, 260)
        );

        painel.add(
                tabelaRolagem,
                BorderLayout.CENTER
        );

        JScrollPane detalhesRolagem =
                new JScrollPane(detalhes);

        detalhesRolagem.setPreferredSize(
                new Dimension(990, 210)
        );

        painel.add(
                detalhesRolagem,
                BorderLayout.SOUTH
        );

        // SELECIONAR O PRIMEIRO REGISTRO
        tabela.setRowSelectionInterval(0, 0);

        JOptionPane.showMessageDialog(
                janela,
                painel,
                "Ocorrências no PostgreSQL",
                JOptionPane.PLAIN_MESSAGE
        );
    }


    // SOLICITAR ID
    private static Integer pedirId(
            JFrame janela,
            String acao) {

        String texto = JOptionPane.showInputDialog(
                janela,
                "Informe o ID para " + acao + ":"
        );

        if (texto == null) {
            return null;
        }

        try {

            int id = Integer.parseInt(
                    texto.trim()
            );

            if (id > 0) {
                return id;
            }

        } catch (NumberFormatException ignored) {
        }

        throw new IllegalArgumentException(
                "Informe um ID numérico positivo."
        );
    }

    // EDITAR OCORRENCIA
    private static void editar(JFrame janela) {

        Integer id = pedirId(
                janela, "editar"
        );

        if (id == null) {
            return;
        }

        Ocorrencia atual =
                gerenciador.buscarPorId(id);

        if (atual == null) {

            JOptionPane.showMessageDialog(
                    janela,
                    "Ocorrência não encontrada."
            );

            return;
        }

        Ocorrencia nova =
                formulario(janela, atual);

        if (nova != null &&
                gerenciador.alterar(id, nova)) {

            JOptionPane.showMessageDialog(
                    janela,
                    "Ocorrência atualizada com sucesso!"
            );
        }
    }

    // EXCLUIR OCORRENCIA
    private static void excluir(JFrame janela) {

        Integer id = pedirId(
                janela, "excluir"
        );

        if (id == null) {
            return;
        }

        Ocorrencia atual =
                gerenciador.buscarPorId(id);

        if (atual == null) {

            JOptionPane.showMessageDialog(
                    janela,
                    "Ocorrência não encontrada."
            );

            return;
        }

        int escolha = JOptionPane.showConfirmDialog(
                janela,
                "Excluir ocorrência #" + id
                        + " de " + atual.getMunicipio()
                        + "?\nOs atendimentos associados "
                        + "também serão excluídos.",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (escolha == JOptionPane.YES_OPTION &&
                gerenciador.excluir(id)) {

            JOptionPane.showMessageDialog(
                    janela,
                    "Ocorrência excluída com sucesso!"
            );
        }
    }
}
