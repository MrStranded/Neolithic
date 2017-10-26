package enums;

/**
 * This interface ensures certain functionality for text-based enums.
 *
 * Created by michael1337 on 26/10/17.
 */
public interface TextEnumInterface {

	public boolean equals(String other);

	public String toString();

	public TextEnumInterface get(String name);

}
