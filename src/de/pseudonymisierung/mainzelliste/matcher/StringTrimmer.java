/**
 *
 */
package de.pseudonymisierung.mainzelliste.matcher;

import de.pseudonymisierung.mainzelliste.PlainTextField;

/**
 * Removes leading and trailing whitespace from character strings.
 */
public class StringTrimmer extends FieldTransformer<PlainTextField, PlainTextField> {

    @Override
    public PlainTextField transform(PlainTextField input) {
        return new PlainTextField(input.getValue().trim());
    }

    @Override
    public Class<PlainTextField> getInputClass() {
        return PlainTextField.class;
    }

    @Override
    public Class<PlainTextField> getOutputClass() {
        return PlainTextField.class;
    }

}
