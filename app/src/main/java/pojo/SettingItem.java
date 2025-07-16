package pojo;

public class SettingItem {
    private String itemTitle;
    private String itemNavigation;

    public SettingItem() {
    }

    public SettingItem(String itemTitle, String itemNavigation) {
        this.itemTitle = itemTitle;
        this.itemNavigation = itemNavigation;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemNavigation() {
        return itemNavigation;
    }

    public void setItemNavigation(String itemNavigation) {
        this.itemNavigation = itemNavigation;
    }
}
