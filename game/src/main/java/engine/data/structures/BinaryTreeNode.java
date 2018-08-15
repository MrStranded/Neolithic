package engine.data.structures;

import engine.data.IDInterface;
import engine.data.attributes.Attribute;

public class BinaryTreeNode<T extends IDInterface> {

	private BinaryTreeNode<T> left, right;
	private T value;
	private int depth = 0;

	BinaryTreeNode(T value) {
		this.value = value;
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}

	public BinaryTreeNode<T> getLeft() {
		return left;
	}
	public void setLeft(BinaryTreeNode<T> left) {
		this.left = left;
	}

	public BinaryTreeNode<T> getRight() {
		return right;
	}
	public void setRight(BinaryTreeNode<T> right) {
		this.right = right;
	}

	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
}
