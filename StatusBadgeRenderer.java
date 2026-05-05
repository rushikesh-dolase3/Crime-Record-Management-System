import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StatusBadgeRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setOpaque(true);

        String status = value == null ? "" : value.toString().trim().toLowerCase();

        if (status.contains("pending")) {
            label.setBackground(new Color(255, 193, 7)); // yellow
            label.setForeground(Color.BLACK);

        } else if (status.contains("solved")) {
            label.setBackground(new Color(40, 167, 69)); // green
            label.setForeground(Color.WHITE);

        } else if (status.contains("closed") || status.contains("reject")) {
            label.setBackground(new Color(220, 53, 69)); // red
            label.setForeground(Color.WHITE);

        } else if (status.contains("progress") || status.contains("open")) {
            label.setBackground(new Color(0, 123, 255)); // blue
            label.setForeground(Color.WHITE);

        } else {
            label.setBackground(new Color(108, 117, 125)); // gray
            label.setForeground(Color.WHITE);
        }

        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return label;
    }
}