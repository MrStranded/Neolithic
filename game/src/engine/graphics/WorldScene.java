package engine.graphics;


import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.Scene;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.math.Ray3;
import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.ContextGarbageCollector;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;

/**
 * The framework should provide a default implementation of a scene, probably with a root node and a stats node, just
 * like today. Probably, controllers don't belong here, but I also don't think they belong in the Node API.
 */
public final class WorldScene implements Scene {
	private final Node root;

	public WorldScene() {
		root = new Node("root");
	}

	public Node getRoot() {
		return root;
	}

	@Override
	@MainThread
	public boolean renderUnto(final Renderer renderer) {
		// Execute renderQueue item
		GameTaskQueueManager.getManager(ContextManager.getCurrentContext()).getQueue(GameTaskQueue.RENDER)
				.execute(renderer);
		ContextGarbageCollector.doRuntimeCleanup(renderer);

		renderer.draw(root);
		return true;
	}

	@Override
	public PickResults doPick(final Ray3 pickRay) {
		// does nothing.
		return null;
	}
}