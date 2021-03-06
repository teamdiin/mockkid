package br.com.moip.mockkid.variable.resolver;

import br.com.moip.mockkid.model.MockkidRequest;
import br.com.moip.mockkid.model.Regex;
import br.com.moip.mockkid.model.ResponseConfiguration;
import br.com.moip.mockkid.variable.VariableResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexVariableResolver implements VariableResolver {

    private static final Logger logger = LoggerFactory.getLogger(RegexVariableResolver.class);

    @Override
    public boolean handles(String variable) {
        return variable.startsWith("regex.");
    }

    @Override
    public String extract(String variable, ResponseConfiguration responseConfiguration, HttpServletRequest request) {
        if (responseConfiguration.getRegexes() == null) {
            return "";
        }

        Regex regex = matchRegexWithVariable(responseConfiguration.getRegexes(), variable);
        if (regex == null) {
            return "";
        }

        if (regex.getExpression() == null || regex.getExpression().isEmpty()) {
            throw new IllegalArgumentException("Regex expression is empty");
        }

        String body = null;
        try {
            body = ((MockkidRequest)request).getBody();
        } catch (IOException e) {
            logger.error("Error while getting request body for variable {}", variable, e);
            return null;
        }

        Pattern pattern = Pattern.compile(regex.getExpression());
        Matcher matcher = pattern.matcher(body);

        return matchExpressionToRequestBody(matcher);
    }

    private Regex matchRegexWithVariable(List<Regex> regex, String variable) {
        String cleanVariable = variable.replace("regex.", "");

        List<Regex> matchedRegexes = regex.stream()
                .filter(r -> r != null && r.getPlaceholder().equals(cleanVariable))
                .collect(Collectors.toList());

        if (matchedRegexes.isEmpty()) {
            return null;
        }

        if (matchedRegexes.size() > 1) {
            throw new IllegalStateException("There are duplicate placeholders for " + variable);
        }

        return matchedRegexes.get(0);
    }

    private String matchExpressionToRequestBody(Matcher matcher) {
        try {
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (IndexOutOfBoundsException ie) {
            throw new IllegalArgumentException("Missing group in regex expression");
        }

        return "";
    }

}
