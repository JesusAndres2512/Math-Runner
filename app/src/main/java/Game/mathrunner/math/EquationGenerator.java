package Game.mathrunner.math;

import android.content.Context;
import android.webkit.WebView;
import java.util.Random;

public class EquationGenerator {

    private int a, b, c;
    private double[] roots;

    public EquationGenerator() {
        generarFuncionAleatoria();
    }

    /** Genera una nueva ecuación -> compatible con MainActivity */
    public void generateNew() {
        generarFuncionAleatoria();
    }

    /** Devuelve LaTeX de la ecuación actual -> compatible con MainActivity */
    public String getLatexForCurrent() {
        return generarLatex();
    }

    /** Valida la respuesta del usuario */
    public boolean validateUserInput(String input, double tolerance) {
        try {
            double userVal = Double.parseDouble(input);
            if (roots == null || roots.length < 2) return false;
            // Verificar si el valor coincide con alguna raíz dentro de la tolerancia
            if (!Double.isNaN(roots[0]) && Math.abs(userVal - roots[0]) <= tolerance) return true;
            if (!Double.isNaN(roots[1]) && Math.abs(userVal - roots[1]) <= tolerance) return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Genera la ecuación aleatoria */
    public void generarFuncionAleatoria() {
        Random random = new Random();
        do {
            a = random.nextInt(11) - 5; // -5 a 5
        } while (a == 0); // evitar a=0
        b = random.nextInt(11) - 5;
        c = random.nextInt(11) - 5;

        calcularRaices();
    }

    /** Calcula las raíces reales (NaN si complejas) */
    private void calcularRaices() {
        double discriminante = b * b - 4 * a * c;

        if (discriminante >= 0) {
            double sqrtDisc = Math.sqrt(discriminante);
            double x1 = (-b + sqrtDisc) / (2 * a);
            double x2 = (-b - sqrtDisc) / (2 * a);
            roots = new double[]{x1, x2};
        } else {
            roots = new double[]{Double.NaN, Double.NaN};
        }
    }

    /** Devuelve la ecuación en formato LaTeX */
    public String generarLatex() {
        StringBuilder sb = new StringBuilder("f(x) = ");

        sb.append(a).append("x^2 ");
        if (b >= 0) sb.append("+ ").append(b).append("x ");
        else sb.append("- ").append(-b).append("x ");

        if (c >= 0) sb.append("+ ").append(c);
        else sb.append("- ").append(-c);

        return sb.toString();
    }

    /** Retorna las raíces */
    public double[] getRoots() {
        return roots;
    }

    /** Retorna los coeficientes */
    public int[] getCoefficients() {
        return new int[]{a, b, c};
    }

    /** Método auxiliar para mostrar la ecuación en un WebView */
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
