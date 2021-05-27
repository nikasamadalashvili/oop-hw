public class MetropolisFilterModel {
    private PopulationSearchType populationSearchType;
    private KeywordSearchType keywordSearchType;
    private String metropolis;
    private String continent;
    private String population;

    public MetropolisFilterModel(PopulationSearchType populationSearchType, KeywordSearchType keywordSearchType, String metropolis, String continent, String population){
        this.populationSearchType = populationSearchType;
        this.keywordSearchType = keywordSearchType;
        this.metropolis = metropolis;
        this.continent = continent;
        this.population = population;
    }

    public PopulationSearchType getPopulationSearchType() {
        return populationSearchType;
    }

    public KeywordSearchType getKeywordSearchType() {
        return keywordSearchType;
    }

    public String getMetropolis() {
        return metropolis;
    }

    public String getContinent() {
        return continent;
    }

    public String getPopulation() {
        return population;
    }
}
enum PopulationSearchType {
    GREATER,
    SMALLER
}

enum KeywordSearchType {
    EXACT,
    PARTIAL
}
