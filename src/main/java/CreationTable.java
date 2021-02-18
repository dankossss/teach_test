import java.sql.ResultSet;
import java.sql.SQLException;

public class CreationTable {

    public static void drawTableView(String[] columnName, ResultSet setValue, ResultSet maxLengthLine) throws SQLException {
        int[] width = new int[columnName.length];
        for (int i = 0; i < columnName.length; i ++) width[i] = columnName[i].length();
        while (maxLengthLine.next()) {
            for (int i = 0; i < width.length; i++) width[i] = Math.max(width[i], maxLengthLine.getInt(i + 1));
        }

        drawLine(width);
        String hat = "  |";
        for (int i = 0; i < columnName.length; i++ ) {
            hat += "  " + columnName[i];
            for (int j = 0; j < (width[i] + 2) - columnName[i].length(); j++) hat +=" ";
            hat += "|";
        }
        System.out.println(hat);
        drawLine(width);
        String str = "";
        while (setValue.next()) {
            str = "  |";
            for (int i = 1; i <= width.length; i++) {
                str += "  " + setValue.getString(i);
                for (int j = 0; j < (width[i - 1] + 2) - setValue.getString(i).length(); j++) str += " ";
                str += "|";
            }
            System.out.println(str);
        }
        drawLine(width);
        System.out.println("");
    }

    public static void drawLine(int[] width) {
        String line = "  -";
        for (int i = 0; i < width.length; i ++) {
            for (int j = 0; j < width[i]+5; j++) {
                line = line + "-";
            }
        }
        System.out.println(line);
    }
}
