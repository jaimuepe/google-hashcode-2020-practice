package com.nevergoes.hashcode.parser;

public class Row {

	private final String[] data;

	public Row(String[] data) {
		this.data = data;
	}

	/**
	 * Returns the value at the specified column casted to the specified class, if
	 * possible.
	 * 
	 * @param column Column of the element.
	 * @param clazz  Class used to cast the element. Will fail if conversion is not
	 *               possible.
	 * @param <T>    Required type for generics.
	 * @return element at the specified position, casted to the desired type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T valueAt(int column, Class<T> clazz) {

		final String val = data[column];

		if (clazz == String.class) {
			return (T) val;
		}

		if (clazz == int.class) {
			return (T) (Object) Integer.parseInt(val);
		}

		if (clazz == Integer.class) {
			return (T) Integer.valueOf(Integer.parseInt(val));
		}

		if (clazz == char.class) {
			return (T) (Object) val.charAt(0);
		}

		if (clazz == Character.class) {
			return (T) Character.valueOf(val.charAt(0));
		}

		if (clazz == float.class) {
			return (T) (Object) Float.parseFloat(val);
		}

		if (clazz == Float.class) {
			return (T) Float.valueOf(Float.parseFloat(val));
		}

		System.out.println("I don't know how to handle the type: " + clazz.getName() + " sorry :(");

		return null;
	}

}
