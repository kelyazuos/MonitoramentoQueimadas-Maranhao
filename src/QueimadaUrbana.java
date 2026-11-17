
public class QueimadaUrbana extends Ocorrencia {

    public QueimadaUrbana(
            int id,
            int idMunicipio,
            String municipio,
            int idAgente,
            String data,
            double temperatura,
            double umidade,
            String descricao,
            String situacao,
            String bairro) {

        super(
                id,
                idMunicipio,
                municipio,
                idAgente,
                data,
                temperatura,
                umidade,
                descricao,
                situacao,
                bairro
        );
    }

    @Override
    public double calcularRisco() {

        double risco =
                getTemperatura() * 1.2
                        - getUmidade() * 0.3;

        return Math.max(
                0,
                Math.min(100, risco)
        );
    }
}
