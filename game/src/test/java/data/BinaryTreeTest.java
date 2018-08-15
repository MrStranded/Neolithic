package data;

import engine.data.IDInterface;
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

		Attribute[] attributes = new Attribute[5];
		attributes[0] = new Attribute(8);
		attributes[2] = new Attribute(14);
		attributes[3] = new Attribute(-1);

		tree.insert(attributes);

		assertTrue(tree.get(8) != null);
		assertTrue(tree.get(14) != null);
		assertTrue(tree.get(-1) != null);
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
	public void testBinaryTreeMergeWithTree() {
		BinaryTree t1 = new BinaryTree();

		t1.insert(new Attribute(0));
		t1.insert(new Attribute(1));
		t1.insert(new Attribute(2));

		BinaryTree t2 = new BinaryTree();

		t2.insert(new Attribute(1));
		t2.insert(new Attribute(3));
		t2.insert(new Attribute(-1));

		t1.insert(t2);

		assertTrue(t1.get(3) != null);
		assertEquals(5, t1.size());
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
		assertEquals(null, tree.get(0));
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

	@Test
	public void testBinaryTreeRemoval() {
		BinaryTree tree = new BinaryTree();

		tree.insert(new Attribute(0));
		tree.insert(new Attribute(1));
		tree.insert(new Attribute(2));
		tree.insert(new Attribute(3));
		tree.insert(new Attribute(4));

		tree.remove(1);
		tree.remove(3);
		tree.remove(4);
		tree.remove(0);
		tree.remove(2);
		tree.remove(1);

		assertEquals(0, tree.size());
	}

	@Test
	public void testBinaryTreeToArray() {
		BinaryTree<Attribute> tree = new BinaryTree();

		tree.insert(new Attribute(100));
		tree.insert(new Attribute(-21));
		tree.insert(new Attribute(11));
		tree.insert(new Attribute(31));
		tree.insert(new Attribute(0));

		IDInterface[] attributes = tree.toArray();

		// checking wheter the array is sorted from lowest to highest
		for (int i=0; i<attributes.length-1; i++) {
			assertTrue(attributes[i].getId() < attributes[i+1].getId());
		}

		assertEquals(attributes.length, tree.size());
	}
}
