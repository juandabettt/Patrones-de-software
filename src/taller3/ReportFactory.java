package taller3;

public class ReportFactory {
    
    // Factory Method: Returns the appropriate object based on type
    public static ReportGenerator getReport(String type) {
        if (type == null) {
            return null;
        }
        
        // Using modern switch expression (Java 14+)
        return switch (type.toUpperCase()) {
            case "PDF" -> new PdfReport();
            case "EXCEL" -> new ExcelReport();
            case "JSON" -> new JsonReport();
            default -> throw new IllegalArgumentException("Unsupported report format: " + type);
        };
    }
}