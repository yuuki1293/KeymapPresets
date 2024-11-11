package yuuki1293.keymappresets.common;

public enum SortOrder {
    asc,
    desc;

    public String getString() {
        return switch (this) {
            case asc -> "⬆";
            case desc -> "⬇";
        };
    }
}
