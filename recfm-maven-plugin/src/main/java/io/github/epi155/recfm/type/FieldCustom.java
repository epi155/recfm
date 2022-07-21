package io.github.epi155.recfm.type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class FieldCustom extends FloatingField implements SelfCheck {
    private char padChar = ' ';
    private char initChar = ' ';
    private CheckUser check = CheckUser.Ascii;
    private AlignMode align = AlignMode.LFT;
    private String regex;

    @Override
    public String picture() {
        return "X";
    }

    @Override
    public void selfCheck() {
        if (regex != null) {
            try {
                Pattern.compile(regex);
            } catch (PatternSyntaxException e) {
                throw new ClassDefineException(fieldDescriptor() + " invalid regex: " + e.getMessage());
            }
        }
    }

    private String fieldDescriptor() {
        return "Field " + getName() + "@" + getOffset() + "+" + getLength();
    }
}
