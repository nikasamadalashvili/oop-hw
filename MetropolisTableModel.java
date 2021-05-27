import javax.swing.table.AbstractTableModel;
import java.sql.*;
import java.util.ArrayList;

public class MetropolisTableModel extends AbstractTableModel {
    private Connection connection;
    private Statement statement;
    private ArrayList<MetropolisRowModel> metropolises;

    protected String[] columnNames = new String[] { "Metropolis", "Continent", "Population"};
    protected Class[] columnClasses = new Class[] { String.class, String.class, Long.class};

    public MetropolisTableModel(String host, String userName, String password, String database) {
        metropolises = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection
                    ( "jdbc:mysql://" + host, userName ,password);
            statement = connection.createStatement();
            statement.executeQuery("USE " + database);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addData(String metropolis, String continent, String population) {
        var query = "insert into metropolises (metropolis, continent, population) values (\"%s\", \"%s\", %s);";
        query = String.format(query, metropolis, continent, population);
        int result = 0;
        try {
            result = statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        metropolises.clear();
        Long populationExact = null;
        try {
            populationExact = Long.parseLong(population);
        }
        catch (Exception ex) { }
        metropolises.add(new MetropolisRowModel(metropolis, continent, populationExact));
        fireTableDataChanged();
    }

    public void updateData(MetropolisFilterModel filterModel) {
        metropolises.clear();
        var query = "SELECT * FROM metropolises %s";
        var whereClause = "";

        var metropolis = filterModel.getMetropolis();
        if (metropolis != null && !metropolis.isEmpty()) {
            var value = filterModel.getKeywordSearchType() == KeywordSearchType.EXACT ? metropolis : "%" + metropolis + "%";
            whereClause = addCondition(whereClause, "AND", "metropolis like " + "\"" + value + "\"");
        }
        var continent = filterModel.getContinent();
        if (continent != null && !continent.isEmpty()) {
            var value = filterModel.getKeywordSearchType() == KeywordSearchType.EXACT ? continent : "%" + continent + "%";
            whereClause = addCondition(whereClause, "AND", "continent like " + "\"" + value + "\"");
        }
        var populationStr = filterModel.getPopulation();
        if (populationStr != null && !populationStr.isEmpty()) {
            try {
                var population = Integer.parseInt(populationStr);
                var value = filterModel.getPopulationSearchType() == PopulationSearchType.GREATER ? ">= " + populationStr : "<= " + populationStr;
                whereClause = addCondition(whereClause, "AND", "population " + value);
            }
            catch (Exception ex) { }
        }

        query = String.format(query, whereClause);

        try {
            var data = statement.executeQuery(query);
            fillMetropolisesFromResultSet(data);
            fireTableDataChanged();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void fillMetropolisesFromResultSet(ResultSet set) throws SQLException {
        while (set.next()) {
            var newMetropolis = new MetropolisRowModel(set.getString("metropolis"), set.getString("continent"), set.getLong("population"));
            metropolises.add(newMetropolis);
        }
    }

    private String addCondition(String clause, String appender, String condition)
    {
        if (clause.length() <= 0)
        {
            return String.format("WHERE %s",condition);
        }
        return String.format("%s %s %s", clause, appender, condition);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public int getRowCount() {
        return metropolises.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case 0: return metropolises.get(row).metropolis;
            case 1: return metropolises.get(row).continent;
            case 2: return metropolises.get(row).population;
            default:
                return null;
        }
    }

    private class MetropolisRowModel {
        private String metropolis;
        private String continent;
        private Long population;

        private MetropolisRowModel(String metropolis, String continent, Long population) {
            this.metropolis = metropolis;
            this.continent = continent;
            this.population = population;
        }

        public String getMetropolis() {
            return metropolis;
        }

        public String getContinent() {
            return continent;
        }

        public Long getPopulation() {
            return population;
        }
    }
}
