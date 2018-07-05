package main.threads;

import main.data.Data;
import main.data.proto.Container;
import main.data.proto.ProtoAttribute;
import main.data.proto.Value;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This class handles the Parser activities in a seperate thread.
 *
 * Created by Michael on 14.08.2017.
 */
public class ParserTestThread extends DependantThread {

	public void run() {

		// ¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦
		// ¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦ parser test ¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦
		// ¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦

		waitForDependantThread();
		System.out.println("Parser finished.");

		for (Container container : Data.getContainers()) {
			System.out.println("Container "+container.getType()+","+container.getTextId()+"("+container.getId()+")");
			ConcurrentLinkedDeque<Value> values = container.getValues();
			for (Value value : values) {
				System.out.print("   Value "+value.getName()+"=");
				int i = 0;
				String s = null;
				while ((s = value.tryToGetString(i))!=null) {
					System.out.print(" "+s+",");
					i++;
				}
				System.out.println("");
			}
		}

		for (ProtoAttribute protoAttribute : Data.getProtoAttributes()) {
			System.out.println("Attribute "+protoAttribute.getName()+","+protoAttribute.getTextId()+"("+protoAttribute.isFlag()+","+protoAttribute.canMutate()+")");
		}

	}

}