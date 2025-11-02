package Game.mathrunner.math;

public class EquationValidator {

    private static final double TOLERANCIA = 2.0;

    /**
     * Valida si la respuesta del usuario (una raíz o ambas)
     * coincide con las raíces reales de la ecuación dentro de una tolerancia.
     *
     * @param inputRespuesta texto ingresado por el usuario (ej: "2", "-1, 3")
     * @param raices verdaderas raíces calculadas
     * @return true si es correcta
     */
    public static boolean validarRespuesta(String inputRespuesta, double[] raices) {
        if (inputRespuesta == null || inputRespuesta.trim().isEmpty()) return false;
        if (raices == null) return false;

        try {
            String[] partes = inputRespuesta.replace(" ", "").split(",");
            if (raices[0] == Double.NaN || raices[1] == Double.NaN)
                return false; // no se pueden validar raíces complejas

            // Parsear entrada del jugador
            double[] respuestasUsuario = new double[partes.length];
            for (int i = 0; i < partes.length; i++) {
                respuestasUsuario[i] = Double.parseDouble(partes[i]);
            }

            // Comparar con tolerancia
            for (double resp : respuestasUsuario) {
                boolean coincide = false;
                for (double raizReal : raices) {
                    if (Math.abs(resp - raizReal) <= TOLERANCIA) {
                        coincide = true;
                        break;
                    }
                }
                if (!coincide) return false;
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}

