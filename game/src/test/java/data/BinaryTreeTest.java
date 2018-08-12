package data;

import engine.data.attributes.Attribute;
import engine.data.structures.BinaryTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BinaryTreeTest {

	@Test
	public void testBinaryTreeCreation() {
		BinaryTree tree = new BinaryTree();

		tree.insert(new Attribute(1));
		tree.insert(new Attribute(2));
		tree.insert(new Attribute(3));
		tree.insert(new Attribute(4));
		tree.insert(new Attribute(5));
		tree.insert(new Attribute(6));
		tree.insert(new Attribute(7));
		tree.insert(new Attribute(8));
		tree.insert(new Attribute(9));
		tree.insert(new Attribute(10));
		tree.insert(new Attribute(11));
		tree.insert(new Attribute(12));

		System.out.println(tree);

		assertTrue(true);
	}

}
