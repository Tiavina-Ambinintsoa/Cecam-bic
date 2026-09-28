package mg.cecam.bic.common.util;

public final class ScoreColorMapper {
    private ScoreColorMapper() {}

    public static String toHex(String couleur) {
        return switch (couleur) {
            case "Chartreuse" -> "#99d26c";
            case "Jaune moutarde" -> "#c7cd41";
            case "Jaune Or" -> "#f7ca17";
            case "Rouge verdâtre" -> "#e67213";
            case "Rouge" -> "#d71d0e ";
            default -> "#94A3B8";
        };
    }
}