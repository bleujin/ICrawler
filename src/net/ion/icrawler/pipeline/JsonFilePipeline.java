package net.ion.icrawler.pipeline;

import com.alibaba.fastjson.JSON;

import net.ion.icrawler.ResultItems;
import net.ion.icrawler.Task;
import net.ion.icrawler.utils.FilePersistentBase;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
			printWriter.write(JSON.toJSONString(resultItems.getAll()));
			printWriter.close();
		} catch (IOException e) {
			logger.warn("write file error", e);
		}
	}
}
