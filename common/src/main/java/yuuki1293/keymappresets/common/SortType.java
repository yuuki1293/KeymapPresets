package yuuki1293.keymappresets.common;

public enum SortType {
    MODIFIED,
    CREATED,
    NAME;

    public String getString() {
        return this.toString().toLowerCase();
    }
}
