package letshangllc.uncrecattendence.objects;

/**
 * Created by Carl on 8/7/2016.
 */
public class FacilitySelectItem {
    private String name;
    private boolean showResult;

    public FacilitySelectItem(String name, boolean showResult) {
        this.name = name;
        this.showResult = showResult;
    }

    public String getName() {
        return name;
    }

    public void setShowResult(boolean showResult) {
        this.showResult = showResult;
    }

    public boolean isShowResult() {
        return showResult;
    }
}
