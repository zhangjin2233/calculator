import java.util.List;
import java.util.Map;

public class PDCDataSet {
    private int rowCount;
    private Map row;
    private List<Map> list;

    public Map getRow(int i) {
        return list.get(i);
    }

    public Map getRow() {
        return row;
    }

    public int getRowCount() {
        return rowCount;
    }
}
