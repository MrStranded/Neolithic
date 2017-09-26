package threads;

import data.Data;
import data.personal.Attribute;
import data.proto.Container;
import data.proto.ProtoAttribute;
import data.proto.Value;
import parser.Parser;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class handles the Parser activities in a seperate thread.
 *
 * Created by Michael on 14.08.2017.
 */
public class ParserTestThread extends Thread {

	public void run() {

		// ¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦
		// ¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦ parser test ¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦
		// ¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦¦

		double lastProgress = -1;
		while (!Parser.isFinished()) {
			if (lastProgress != Parser.getProgress()) {
				lastProgress = Parser.getProgress();
				System.out.println("Parser progress: "+lastProgress);
			}

			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Parser finished.");

		ConcurrentLinkedDeque<Container> containers = Data.getContainers();
		for (Container container : containers) {
			System.out.println("Container "+container.getType()+","+container.getTextId()+"("+container.getId()+")");
			ConcurrentLinkedDeque<Value> values = container.getValues();
			for (Value value : values) {
				System.out.println("   Value "+value.getName()+"=");
				int i = 0;
				String s = null;
				while ((s = value.tryToGetString(i))!=null) {
					System.out.println("      >"+s+"<");
					i++;
				}
			}
		}
		ConcurrentLinkedDeque<ProtoAttribute> protoAttributes = Data.getProtoAttributes();
		for (ProtoAttribute protoAttribute : protoAttributes) {
			System.out.println("Attribute "+protoAttribute.getName()+","+protoAttribute.getTextId()+"("+protoAttribute.isFlag()+","+protoAttribute.canMutate()+")");
		}

	}

}
