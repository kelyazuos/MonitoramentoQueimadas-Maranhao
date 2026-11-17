
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/monitoramento_queimadas_teste";

    private static final String USUARIO = "postgres";

    public static Connection conectar(String senha)
            throws SQLException {

        return DriverManager.getConnection(
                URL, USUARIO, senha
        );
    }
}

