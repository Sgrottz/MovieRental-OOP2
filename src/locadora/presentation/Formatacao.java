package locadora.presentation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import locadora.domain.RegraNegocioException;

/** Classe estática com formatação de datas e valores para exibição (usada pelas duas interfaces). */
public final class Formatacao {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private Formatacao() {
    }

    public static String data(LocalDate data) {
        return data == null ? "-" : DATA.format(data);
    }

    public static String moeda(double valor) {
        return String.format(PT_BR, "R$ %.2f", valor);
    }

    /** Converte "dd/MM/aaaa". Texto vazio devolve null (significa "hoje" para o serviço). */
    public static LocalDate interpretarData(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim(), DATA);
        } catch (DateTimeParseException e) {
            throw new RegraNegocioException("Data inválida. Use o formato dd/MM/aaaa.");
        }
    }
}
