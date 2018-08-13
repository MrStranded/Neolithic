package data;

import engine.data.attributes.Attribute;
import engine.data.structures.BinaryTree;
import org.junit.jupiter.api.Test;
import org.lwjgl.system.CallbackI;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BinaryTreeTest {

	@Test
	public void testBinaryTreeCreation() {
		BinaryTree tree = new BinaryTree();

		int n = 100;
		long time = System.nanoTime();
		for (int i=0; i<n; i++) {
			int m = 1;//((i & 1) == 1)? -1 : 1;
			tree.insert(new Attribute(i*m));
		}
		time = System.nanoTime() - time;

		System.out.println(tree);
		System.out.println("inserting " + n + " attributes took " + (time/1000000d) + " ms");
		System.out.println("depth: " + tree.getDepth());
		System.out.println("size: " + tree.size());

		assertTrue(true);
	}

}
