
public abstract class Ocorrencia {

    private int id;
    private final int idMunicipio;
    private final String municipio;
    private final int idAgente;
    private final String data;
    private final double temperatura;
    private final double umidade;
    private final String descricao;
    private final String situacao;
    private final String detalhe;

    protected Ocorrencia(
            int id,
            int idMunicipio,
            String municipio,
            int idAgente,
            String data,
            double temperatura,
            double umidade,
            String descricao,
            String situacao,
            String detalhe) {

        this.id = id;
        this.idMunicipio = idMunicipio;
        this.municipio = municipio;
        this.idAgente = idAgente;
        this.data = data;
        this.temperatura = temperatura;
        this.umidade = umidade;
        this.descricao = descricao;
        this.situacao = situacao;
        this.detalhe = detalhe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public String getMunicipio() {
        return municipio;
    }

    public int getIdAgente() {
        return idAgente;
    }

    public String getData() {
        return data;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public double getUmidade() {
        return umidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getSituacao() {
        return situacao;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public abstract double calcularRisco();
}
