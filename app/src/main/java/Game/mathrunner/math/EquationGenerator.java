package Game.mathrunner.math;

import android.content.Context;
import android.webkit.WebView;
import java.util.Random;

public class EquationGenerator {

    private int a, b, c;
    private double[] roots;
    private final Random random = new Random();

    public EquationGenerator() {
        generarFuncionAleatoria();
    }

    /** Genera una nueva ecuación aleatoria */
    public void generateNew() {
        generarFuncionAleatoria();
    }

    /** Devuelve la ecuación en formato LaTeX */
    public String getLatexForCurrent() {
        return generarLatex();
    }

    /** Valida la respuesta del usuario */
    public boolean validateUserInput(String input, double tolerance) {
        try {
            double userVal = Double.parseDouble(input);
            if (roots == null || roots.length < 2) return false;
            // Verificar coincidencia con alguna raíz real
            for (double r : roots) {
                if (!Double.isNaN(r) && Math.abs(userVal - r) <= tolerance) return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Genera una ecuación con mayor variedad */
    public void generarFuncionAleatoria() {
        int tipo = random.nextInt(4); // 0..3 → 4 tipos de generación

        switch (tipo) {
            case 0:
                // 🔹 Generar ecuación con raíces reales aleatorias
                generarDesdeRaicesReales(

                );
                break;
            case 1:
                // 🔹 Generar ecuación con raíces enteras pequeñas
                generarDesdeRaicesEnteras();
                break;
            case 2:
                // 🔹 Ecuación con raíces iguales (discriminante = 0)
                generarConRaicesIguales();
                break;
            default:
                // 🔹 Ecuación completamente aleatoria
                generarCompletamenteAleatoria();
                break;
        }

        calcularRaices();
    }

    /** Tipo 1: Genera ecuación desde raíces reales */
    private void generarDesdeRaicesReales() {
        double r1 = random.nextInt(9) - 4; // -4 a 4
        double r2 = random.nextInt(9) - 4;
        if (r1 == r2) r2 += 1; // asegurar raíces diferentes
        if (a == 0) a = 1;
        a = random.nextInt(3) + 1; // a = 1..3
        b = (int) (-a * (r1 + r2));
        c = (int) (a * r1 * r2);

    }

    /** Tipo 2: Ecuación con raíces enteras */
    private void generarDesdeRaicesEnteras() {
        int r1 = random.nextInt(7) - 3; // -3..3
        int r2 = random.nextInt(7) - 3;
        if (r1 == r2) r2 += 1;
        a = 1;
        b = -(r1 + r2);
        c = r1 * r2;
    }

    /** Tipo 3: Raíces dobles (discriminante 0) */
    private void generarConRaicesIguales() {
        int r = random.nextInt(6) - 3;
        a = random.nextInt(3) + 1;
        b = -2 * a * r;
        c = a * r * r;
    }

    /** Tipo 4: Completamente aleatoria */
    private void generarCompletamenteAleatoria() {
        do {
            a = random.nextInt(9) - 4; // -4..4
        } while (a == 0);
        b = random.nextInt(19) - 9; // -9..9
        c = random.nextInt(19) - 9;
    }

    /** Calcula raíces reales (NaN si no existen) */
    private void calcularRaices() {
        double disc = b * b - 4.0 * a * c;
        if (disc >= 0) {
            double sqrtDisc = Math.sqrt(disc);
            roots = new double[]{
                    (-b + sqrtDisc) / (2 * a),
                    (-b - sqrtDisc) / (2 * a)
            };
        } else {
            roots = new double[]{Double.NaN, Double.NaN};
        }
    }

    /** Devuelve ecuación en formato LaTeX */
    public String generarLatex() {
        StringBuilder sb = new StringBuilder();

        sb.append(a).append("x^2 ");
        sb.append(b >= 0 ? "+ " + b : "- " + (-b)).append("x ");
        sb.append(c >= 0 ? "+ " + c : "- " + (-c));

        return sb.toString();
    }

    /** Retorna raíces */
    public double[] getRoots() {
        return roots;
    }

    /** Retorna coeficientes */
    public int[] getCoefficients() {
        return new int[]{a, b, c};
    }

    /** Muestra ecuación en WebView (opcional, modo demostración) */
    public void mostrarEnWebView(Context context, WebView webView) {
        String ecuacionLatex = generarLatex();
        String html = "<html>" +
                "<head>" +
                "<script type='text/x-mathjax-config'>" +
                "MathJax.Hub.Config({tex2jax: {inlineMath: [['$','$']]}});" +
                "</script>" +
                "<script src='https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS-MML_HTMLorMML'></script>" +
                "</head>" +
                "<body style='text-align:center; font-size:24px; background-color:#FFFFFF;'>" +
                "<p>Resuelve la ecuación:</p>" +
                "<p>$$" + ecuacionLatex + "$$</p>" +
                "</body></html>";

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }
}
