package net.ion.icrawler.pipeline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import net.ion.framework.parse.gson.JsonParser;
import net.ion.icrawler.Task;
import net.ion.icrawler.model.HasKey;
import net.ion.icrawler.utils.FilePersistentBase;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store results objects (page models) to files in JSON format.<br>
 * Use model.getKey() as file name if the model implements HasKey.<br>
 * Otherwise use SHA1 as file name.
 * @since 0.2.0
 */
public class JsonFilePageModelPipeline extends FilePersistentBase implements PageModelPipeline {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public JsonFilePageModelPipeline() {
		setPath("./resource/temp");
	}

	public JsonFilePageModelPipeline(String path) {
		setPath(path);
	}

	@Override
	public void process(Object o, Task task) {
		String path = this.path + "/" + task.getUUID() + "/";
		try {
			String filename;
			if (o instanceof HasKey) {
				filename = path + ((HasKey) o).key() + ".json";
			} else {
				filename = path + DigestUtils.md5Hex(ToStringBuilder.reflectionToString(o)) + ".json";
			}
			File file = new File(filename);
			if (! file.getParentFile().exists()){
				file.getParentFile().mkdirs() ;
			}
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			printWriter.write(JsonParser.fromObject(o).toString());
			printWriter.close();
		} catch (IOException e) {
			logger.warn("write file error", e);
		}
	}
}
