package net.ion.icrawler.pipeline;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Task;
import net.ion.icrawler.utils.FilePersistentBase;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store results to files in JSON format.<br>
 */
public class JsonFilePipeline extends FilePersistentBase implements Pipeline {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public JsonFilePipeline() {
		setPath("./resource/temp");
	}

	public JsonFilePipeline(String path) {
		setPath(path);
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		String path = this.path + "/" + task.getUUID() + "/";
		try {
			PrintWriter printWriter = new PrintWriter(new FileWriter(getFile(path + DigestUtils.md5Hex(resultItems.getRequest().getUrl()) + ".json")));
			printWriter.write(JsonObject.fromObject(resultItems.getAll()).toString());
			printWriter.close();
		} catch (IOException e) {
			logger.warn("write file error", e);
		}
	}
}
