package paa;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;

public final class ChartUtil {
    public static final class Series {
        public final String name;
        public final double[] x;
        public final double[] y;
        public final Color color;

        public Series(String name, double[] x, double[] y, Color color) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    private ChartUtil() {
    }

    public static void saveLineChart(Path file, String title, String xLabel, String yLabel, List<Series> seriesList) throws IOException {
        int width = 1200;
        int height = 750;
        int left = 90;
        int right = 220;
        int top = 80;
        int bottom = 90;

        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Series series : seriesList) {
            for (int i = 0; i < series.x.length; i++) {
                minX = Math.min(minX, series.x[i]);
                maxX = Math.max(maxX, series.x[i]);
                minY = Math.min(minY, series.y[i]);
                maxY = Math.max(maxY, series.y[i]);
            }
        }

        if (Double.isInfinite(minX) || Double.isInfinite(minY)) {
            return;
        }

        if (minX == maxX) {
            minX -= 1.0;
            maxX += 1.0;
        }
        if (minY == maxY) {
            minY -= 1.0;
            maxY += 1.0;
        }

        double xPadding = (maxX - minX) * 0.05;
        double yPadding = (maxY - minY) * 0.10;
        minX -= xPadding;
        maxX += xPadding;
        minY -= yPadding;
        maxY += yPadding;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            g.setColor(new Color(245, 245, 245));
            g.fillRoundRect(20, 20, width - 40, height - 40, 24, 24);

            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("SansSerif", Font.BOLD, 24));
            FontMetrics titleMetrics = g.getFontMetrics();
            g.drawString(title, (width - titleMetrics.stringWidth(title)) / 2, 55);

            int plotX = left;
            int plotY = top;
            int plotW = width - left - right;
            int plotH = height - top - bottom;

            g.setColor(Color.WHITE);
            g.fillRect(plotX, plotY, plotW, plotH);
            g.setColor(new Color(220, 220, 220));
            g.drawRect(plotX, plotY, plotW, plotH);

            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g.setColor(new Color(180, 180, 180));
            int gridLines = 5;
            for (int i = 0; i <= gridLines; i++) {
                int y = plotY + plotH - (int) Math.round((plotH * i) / (double) gridLines);
                g.drawLine(plotX, y, plotX + plotW, y);
                double value = minY + (maxY - minY) * i / gridLines;
                String label = formatNumber(value);
                int labelWidth = g.getFontMetrics().stringWidth(label);
                g.setColor(Color.DARK_GRAY);
                g.drawString(label, plotX - 12 - labelWidth, y + 5);
                g.setColor(new Color(180, 180, 180));
            }

            for (int i = 0; i <= gridLines; i++) {
                int x = plotX + (int) Math.round((plotW * i) / (double) gridLines);
                g.drawLine(x, plotY, x, plotY + plotH);
                double value = minX + (maxX - minX) * i / gridLines;
                String label = formatNumber(value);
                int labelWidth = g.getFontMetrics().stringWidth(label);
                g.setColor(Color.DARK_GRAY);
                g.drawString(label, x - labelWidth / 2, plotY + plotH + 24);
                g.setColor(new Color(180, 180, 180));
            }

            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString(xLabel, plotX + plotW / 2 - g.getFontMetrics().stringWidth(xLabel) / 2, height - 35);
            g.rotate(-Math.PI / 2);
            g.drawString(yLabel, -(plotY + plotH / 2 + g.getFontMetrics().stringWidth(yLabel) / 2), 40);
            g.rotate(Math.PI / 2);

            for (Series series : seriesList) {
                if (series.x.length == 0) {
                    continue;
                }
                g.setColor(series.color);
                g.setStroke(new BasicStroke(3f));
                Path2D path = new Path2D.Double();
                for (int i = 0; i < series.x.length; i++) {
                    int px = toX(series.x[i], plotX, plotW, minX, maxX);
                    int py = toY(series.y[i], plotY, plotH, minY, maxY);
                    if (i == 0) {
                        path.moveTo(px, py);
                    } else {
                        path.lineTo(px, py);
                    }
                }
                g.draw(path);

                for (int i = 0; i < series.x.length; i++) {
                    int px = toX(series.x[i], plotX, plotW, minX, maxX);
                    int py = toY(series.y[i], plotY, plotH, minY, maxY);
                    g.fillOval(px - 4, py - 4, 8, 8);
                }
            }

            int legendX = width - right + 25;
            int legendY = top + 20;
            g.setFont(new Font("SansSerif", Font.PLAIN, 15));
            g.setColor(Color.DARK_GRAY);
            g.drawString("Legenda", legendX, legendY);
            legendY += 18;
            for (Series series : seriesList) {
                g.setColor(series.color);
                g.fillRect(legendX, legendY + 4, 18, 18);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(legendX, legendY + 4, 18, 18);
                g.drawString(series.name, legendX + 26, legendY + 18);
                legendY += 28;
            }
        } finally {
            g.dispose();
        }

        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        ImageIO.write(image, "png", file.toFile());
    }

    private static int toX(double x, int plotX, int plotW, double minX, double maxX) {
        double normalized = (x - minX) / (maxX - minX);
        return plotX + (int) Math.round(normalized * plotW);
    }

    private static int toY(double y, int plotY, int plotH, double minY, double maxY) {
        double normalized = (y - minY) / (maxY - minY);
        return plotY + plotH - (int) Math.round(normalized * plotH);
    }

    private static String formatNumber(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.0001) {
            return String.valueOf((long) Math.rint(value));
        }
        return String.format(java.util.Locale.US, "%.2f", value);
    }
}
