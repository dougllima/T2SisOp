/**
 * @author Douglas Lima
 *         Parte do projeto T2SisOp
 *         <p>
 *         13/06/2017.
 */
public class Main {
    public static void main(String[] args) {
        // TODO Ler isso de um arquivo
        int[] requestString = {95, 180, 34, 119, 11, 123, 62, 64};
        int numCilindros = 199;
        int initCilindro = 50;
        // TODO Cada classe ler de um arquivo

        FCFS.test(requestString, numCilindros, initCilindro);
        SSTF.test(requestString, numCilindros, initCilindro);
        SCAN.test(requestString, numCilindros, initCilindro);
        LOOK.test(requestString, numCilindros, initCilindro);
        CSCAN.test(requestString, numCilindros, initCilindro);
        CLOOK.test(requestString, numCilindros, initCilindro);
    }
}
