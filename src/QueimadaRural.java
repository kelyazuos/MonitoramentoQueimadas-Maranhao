public class QueimadaRural extends Ocorrencia {

    public QueimadaRural(
            int id,
            int idMunicipio,
            String municipio,
            int idAgente,
            String data,
            double temperatura,
            double umidade,
            String descricao,
            String situacao,
            String tipoVegetacao) {

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
                tipoVegetacao
        );
    }

    @Override
    public double calcularRisco() {

        double risco =
                getTemperatura() * 1.5
                        - getUmidade() * 0.5;

        return Math.max(
                0,
                Math.min(100, risco)
        );
    }
}
