import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.abs;

/**
 * Criado por Douglas Leite
 * Parte do projeto T2SisOp
 * <p>
 * 13/06/2017.
 */
public class SSTF implements DiskScheduler {
    private int[] requests;
    private int numCilindros;
    private int initCilindro;

    private SSTF(int[] requests, int numCilindros, int initCilindro) {
        this.requests = requests;
        this.numCilindros = numCilindros;
        this.initCilindro = initCilindro;
    }

    @Override
    public int serviceRequests() {
        // Clonando o vetor para caso algum metodo altere o original.
        int[] requests = this.requests.clone();

        int result = 0;
        int request = -1;
        int lastIndex = requests.length;
        int dist = numCilindros + 1;
        int position = initCilindro;

        for (int j = 0; j < requests.length; j++) {
            for (int i = 0; i < lastIndex; i++) {
                if (requests[i] > -1) {
                    if (abs(position - requests[i]) < dist) {
                        dist = abs(position - requests[i]);
                        request = i;
                    }
                }
            }
            // Reduz o tamanho do vetor em 1 para o loop interno
            lastIndex--;

            // Adiciona a distancia percorrida no resultado
            result += dist;
            dist = numCilindros + 1;

            // Salva a posição atual
            position = requests[request];
            // Puxa do "fim" do vetor o ultimo numero que ainda não foi processado
            requests[request] = requests[lastIndex];
        }

        return result;
    }

    @Override
    public void printGraph(String filename) {
        // Clonando o vetor para caso algum metodo altere o original.
        int[] requests = this.requests.clone();

        int y_axis = 0;
        int request = -1;
        int lastIndex = requests.length;
        int dist = numCilindros + 1;

        XYSeries series = new XYSeries("SSTF");

        int position = initCilindro;

        /* Adiciona o pontos XY do gráfico de linhas. */
        series.add(y_axis, initCilindro);

        for (int j = 0; j < requests.length; j++) {
            for (int i = 0; i < lastIndex; i++) {
                if (requests[i] > -1) {
                    if (abs(position - requests[i]) < dist) {
                        dist = abs(position - requests[i]);
                        request = i;
                    }
                }
            }
            // Reduz o tamanho do vetor em 1 para o loop interno
            lastIndex--;

            dist = numCilindros + 1;

            // Salva a posição atual
            position = requests[request];
            // Puxa do "fim" do vetor o ultimo numero que ainda não foi processado
            requests[request] = requests[lastIndex];
            series.add(y_axis + ((j + 1)), position);
        }

        /* Adiciona a serie criada a um SeriesCollection. */
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        /* Gera o gráfico de linhas */
        JFreeChart chart = ChartFactory.createXYLineChart(
            /* Title */
                "SSTF Scheduler Algorithm",
            /* Title x*/
                "",
            /* Title y */
                "Cilindro",
                dataset,
            /* Plot Orientation */
                PlotOrientation.HORIZONTAL,
            /* Show Legend */
                false,
            /* Use tooltips */
                false,
            /* Configure chart to generate URLs? */
                false
        );

        /* Configura a espessura da linha do gráfico  */
        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesPaint(0, Color.CYAN);

        plot.setRenderer(renderer);

        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), chart, chartWidth, chartHeight);
        } catch (IOException ex) {
            Logger.getLogger(SSTF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        int[] requests = null;
        int numCilindros = 0;
        int initCilindro = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String line = br.readLine();
            numCilindros = Integer.parseInt(line);

            line = br.readLine();
            initCilindro = Integer.parseInt(line);

            java.util.List<Integer> requestList = new ArrayList<>();
            line = br.readLine();
            for (String s : line.split(" ")) {
                requestList.add(Integer.parseInt(s));
            }
            //iniciando o vetor com o tamanho necessario
            requests = new int[requestList.size()];

            //Transforma a lista para vetor de inteiros
            requests = requestList.stream().mapToInt(i -> i).toArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DiskScheduler SSTF = new SSTF(requests, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos " + SSTF.getClass().getName() + " : " + SSTF.serviceRequests());
        SSTF.printGraph("SSTF.jpg");
    }
}
