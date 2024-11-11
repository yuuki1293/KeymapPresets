package yuuki1293.keymappresets.common;

public enum SortType {
    MODIFIED,
    CREATED,
    NAME;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
