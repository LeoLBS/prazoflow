package br.com.leperber.prazoflow.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public final class DiaUtil {

    private DiaUtil() {
    }

    public static boolean ehDiaUtil(LocalDate data) {
        DayOfWeek diaDaSemana = data.getDayOfWeek();
        return diaDaSemana != DayOfWeek.SATURDAY && diaDaSemana != DayOfWeek.SUNDAY;
    }

    public static LocalDate subtrairDiasUteis(LocalDate data, int diasUteis) {
        LocalDate resultado = data;
        int restantes = diasUteis;
        while (restantes > 0) {
            resultado = resultado.minusDays(1);
            if (ehDiaUtil(resultado)) {
                restantes--;
            }
        }
        return resultado;
    }
}