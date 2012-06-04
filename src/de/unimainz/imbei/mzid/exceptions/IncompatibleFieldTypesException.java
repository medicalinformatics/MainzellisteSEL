package de.unimainz.imbei.mzid.exceptions;

import de.unimainz.imbei.mzid.Field;
import de.unimainz.imbei.mzid.matcher.FieldTransformer;

/** Exception for the case that an FieldTransformer and a Field or two chained FieldTransformers
 * are not compatible respective to their type parameters.
 * @author borg
 *
 */
public class IncompatibleFieldTypesException extends Exception {

	public IncompatibleFieldTypesException(FieldTransformer<Field<?>, Field<?>> first,
			FieldTransformer<Field<?>, Field<?>> second)
	{
		super("Output class of " + first.getClass() +
				" does not match input class of " + second.getClass());
	}

	public IncompatibleFieldTypesException(Field<?> input,
			FieldTransformer<Field<?>, Field<?>> transformer)
	{
		super("Field class " + input.getClass() +
				" does not match input class of " + transformer.getClass());
	}

	public IncompatibleFieldTypesException(FieldTransformer<Field<?>, Field<?>> transformer,
			Field<?> output)
	{
		super("Output class of " + transformer.getClass() +
				" does not match field class " + output.getClass());
	}
}
