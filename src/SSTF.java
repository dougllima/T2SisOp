import com.sun.deploy.util.ArrayUtil;

import static java.lang.Math.abs;

/**
 * Criado por Douglas Leite
 * Parte do projeto T2SisOp
 * <p>
 * 13/06/2017.
 */
public class SSTF implements DiskScheduler {
    private int[] requestString;
    private int numCilindros;
    private int initCilindro;

    public SSTF(int[] requestString, int numCilindros, int initCilindro) {
        this.requestString = requestString;
        this.numCilindros = numCilindros;
        this.initCilindro = initCilindro;
    }

    @Override
    public int serviceRequests() {
        int result = 0;
        int request = -1;
        int lastIndex = requestString.length;
        int dist = numCilindros + 1;
        int position = initCilindro;

        for (int j = 0; j < requestString.length; j++) {
            for (int i = 0; i < lastIndex; i++) {
                if (requestString[i] > -1) {
                    if (abs(position - requestString[i]) < dist) {
                        dist = abs(position - requestString[i]);
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
            position = requestString[request];
            // Puxa do "fim" do vetor o ultimo numero que ainda não foi processado
            requestString[request] = requestString[lastIndex];
        }

        return result;
    }

    @Override
    public void printGraph(String filename) {

    }

    public static void main(String[] args) {
        int[] requestString = {98, 183, 37, 122, 14, 126, 65, 67};
        int numCilindros = 200;
        int initCilindro = 53;

        DiskScheduler fcfs = new SSTF(requestString, numCilindros, initCilindro);
        System.out.println("Número de cilindros percorridos: " + fcfs.serviceRequests());
    }
}
