package yuuki1293.keymappresets.common;

public enum SortOrder {
    ASC,
    DESC;

    @Override
    public String toString() {
        return switch (this) {
            case ASC -> "⬆";
            case DESC -> "⬇";
        };
    }
}
