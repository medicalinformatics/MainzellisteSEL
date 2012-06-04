package de.unimainz.imbei.mzid.matcher;

import java.util.List;
import java.util.Vector;

import org.eclipse.jdt.internal.compiler.ast.TypeParameter;

import de.unimainz.imbei.mzid.Field;
import de.unimainz.imbei.mzid.exceptions.IncompatibleFieldTypesException;

/** Implements a chain of several FieldTransformers applied one after another */
public class FieldTransformerChain {
	
	private List<FieldTransformer> transformers;
	
	public FieldTransformerChain()
	{
		this.transformers = new Vector<FieldTransformer>();
	}

	public Class<?> getInputClass()
	{
		if (this.transformers.size() == 0)
			return null;
		
		return this.transformers.get(0).getInputClass();
	}
	
	public Class<?> getOutputClass()
	{
		if (this.transformers.size() == 0)
			return null;
		
		return this.transformers.get(this.transformers.size() - 1) .getInputClass();
	}

	public void add(FieldTransformer<Field<?>, Field<?>>... toAdd) throws IncompatibleFieldTypesException
	{
		for (FieldTransformer transformer : toAdd)
		{
			// output class of a transformer must be a subclass of input class of the
			// next transformer
			if (this.transformers.size() != 0 && !transformer.getInputClass().isAssignableFrom(this.getOutputClass()))
			{
				throw new IncompatibleFieldTypesException(this.transformers.get(this.transformers.size() - 1), 
						transformer);
			} else
			{
				this.transformers.add(transformer);
			}
		}
	}
	
	public Field<?> transform(Field<?> input)
	{
		Field<?> result = input.clone();
		for (FieldTransformer<Field<?>, Field<?>> transformer : this.transformers)
		{
			result = transformer.transform(result);
		}
		
		return result;
	}

}
