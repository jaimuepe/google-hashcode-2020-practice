package com.nevergoes.hashcode.parser;

import java.lang.reflect.Array;
import java.util.function.Function;

public class Parser {

	private boolean parsed = false;
	private Row[] rows;

	private String separator = " ";

	private final String rawData;

	public Parser(final String rawData) {
		this.rawData = rawData;
	}

	/**
	 * Returns the value at the specified row and column transformed to the
	 * specified class, if possible.
	 * 
	 * @param row    Row of the element.
	 * @param column Column of the element.
	 * @param clazz  Class used to cast the element. Will fail if conversion is not
	 *               possible.
	 * @param <T>    Required type for generics.
	 * @return element at the specified position, casted to the desired type.
	 */
	public <T> T valueAt(int row, int column, Class<T> clazz) {
		lazyParse();
		return rows[row].valueAt(column, clazz);
	}

	/**
	 * Same as {@link #mapRows(int, int, Class, Function)}, but assuming to = 0 (ie.
	 * until the last row).
	 */
	public <T> T[] mapRows(int from, Class<T> clazz, Function<Row, T> mapFcn) {
		lazyParse();
		return mapRows(from, 0, clazz, mapFcn);
	}

	/**
	 * Maps a single row to a destination format.
	 * 
	 * @param row    The index of the row to transform.
	 * @param mapFcn Function to apply to convert the row to the specified class.
	 * @param <T>    Required type for generics.
	 * @return The custom object.
	 */
	public <T> T mapRow(int row, Function<Row, T> mapFcn) {
		lazyParse();
		return mapFcn.apply(rows[row]);
	}

	/**
	 * Maps a subset of rows to a destination format.
	 * 
	 * @param from   first row of the subset. Must be >= 0 and < to.
	 * @param to     last row of the subset. If negative, it will be applied as an
	 *               offset from the last row.
	 * @param clazz  Class of the object we want to generate for each row.
	 * @param mapFcn Mapping function to generate each custom object from a row.
	 * @param <T>    Required type for generics.
	 * @return An array of custom rows with length = to - from.
	 */
	public <T> T[] mapRows(int from, int to, Class<T> clazz, Function<Row, T> mapFcn) {

		lazyParse();

		if (to <= 0) {
			to = rows.length + to;
		}

		int range = to - from;

		@SuppressWarnings("unchecked")
		final T[] tmp = (T[]) Array.newInstance(clazz, range);

		for (int i = 0; i < range; i++) {
			tmp[i] = mapFcn.apply(rows[i + from]);
		}

		return tmp;
	}

	public void setSeparator(String separator) {

		if (this.separator.equals(separator)) {
			this.separator = separator;
			if (parsed) {
				System.out.println(
						"Data was already parsed but the separator changed, so i'm going to mark it as dirty just in case");
				parsed = false;
			}
		}
	}

	private void lazyParse() {

		if (parsed) {
			return;
		}

		final String[] lines = rawData.split("\\r?\\n|\\r");

		rows = new Row[lines.length];

		for (int i = 0; i < lines.length; i++) {

			final String[] dataText = lines[i].split(separator);
			rows[i] = new Row(dataText);
		}

		parsed = true;
	}
}
