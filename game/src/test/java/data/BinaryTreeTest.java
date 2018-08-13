package data;

import engine.data.attributes.Attribute;
import engine.data.structures.BinaryTree;
import org.junit.jupiter.api.Test;
import org.lwjgl.system.CallbackI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BinaryTreeTest {

	@Test
	public void testBinaryTreeInsertion() {
		BinaryTree tree = new BinaryTree();

		tree.insert(new Attribute(1));
		tree.insert(new Attribute(0));
		tree.insert(new Attribute(-100));
		tree.insert(new Attribute(7));

		assertTrue(tree.get(0) != null);
		assertTrue(tree.get(1) != null);
		assertTrue(tree.get(7) != null);
		assertTrue(tree.get(-100) != null);

		assertTrue(tree.get(3) == null);
	}

	@Test
	public void testBinaryTreeMerge() {
		BinaryTree tree = new BinaryTree();

		tree.insert(new Attribute(-1));
		tree.insert(new Attribute(2));
		tree.insert(new Attribute(-3));
		tree.insert(new Attribute(2));

		assertTrue(tree.get(2) != null);
		assertEquals(3, tree.size());
	}

	@Test
	public void testBinaryTreeRetrieval() {
		BinaryTree tree = new BinaryTree();

		Attribute a1 = new Attribute(123);
		Attribute a2 = new Attribute(-123);

		tree.insert(a1);
		tree.insert(a2);

		assertEquals(a1, tree.get(123));
		assertEquals(a2, tree.get(-123));
	}

	@Test
	public void testBinaryTreeSize() {
		BinaryTree tree = new BinaryTree();

		tree.insert(new Attribute(0));
		tree.insert(new Attribute(1));
		tree.insert(new Attribute(2));
		tree.insert(new Attribute(3));
		tree.insert(new Attribute(4));

		assertEquals(5, tree.size());

		tree.insert(new Attribute(-1));
		tree.insert(new Attribute(5));
		tree.insert(new Attribute(100));

		assertEquals(8, tree.size());
	}
}
