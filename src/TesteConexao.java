
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class TesteConexao {

    public static void main(String[] args) {

        JPasswordField campoSenha =
                new JPasswordField();

        int opcao = JOptionPane.showConfirmDialog(
                null,
                campoSenha,
                "Senha do PostgreSQL",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcao != JOptionPane.OK_OPTION) {
            return;
        }

        String senha = new String(
                campoSenha.getPassword()
        );

        try (Connection conexao =
                     Conexao.conectar(senha);

             PreparedStatement comando =
                     conexao.prepareStatement(
                             "select count(*) from ocorrencia"
                     );

             ResultSet resultado =
                     comando.executeQuery()) {

            if (resultado.next()) {

                int total = resultado.getInt(1);

                JOptionPane.showMessageDialog(
                        null,
                        "Conexão realizada com sucesso!\n"
                                + "Ocorrências cadastradas: "
                                + total
                );
            }

        } catch (SQLException erro) {

            JOptionPane.showMessageDialog(
                    null,
                    "Erro na conexão:\n"
                            + erro.getMessage()
            );
        }
    }
}
