package dbsnparser;

import java.util.HashMap;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;

class DBSNPGenotypeInput extends DBSNPInput{

    // ssId:Vector of DBSNPGenotypePopulation objects
    private HashMap ssPopulationMap;

    public DBSNPGenotypeInput() {
        ssPopulationMap = new HashMap();
    }
    public DBSNPGenotypeInput(String rs) {
        this();
        rsId = rs;
    }

    public void addPopulation(String ssId, Vector pops) {
        ssPopulationMap.put(ssId, pops);
    }

    // get the Population Vector for 'ssId'
    public Vector getPopulationsForSS(String ssId) {
        return (Vector)ssPopulationMap.get(ssId);
    }

    // get all  ss to Population mappings for this RS
    public HashMap getSSPopulationsForRs() {
        return ssPopulationMap;
    }
}
