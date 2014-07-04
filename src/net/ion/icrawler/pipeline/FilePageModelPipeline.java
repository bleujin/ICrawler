package net.ion.icrawler.pipeline;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import net.ion.icrawler.Task;
import net.ion.icrawler.model.HasKey;
import net.ion.icrawler.utils.FilePersistentBase;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store results objects (page models) to files in plain format.<br>
 * Use model.getKey() as file name if the model implements HasKey.<br>
 * Otherwise use SHA1 as file name.
 */
public class FilePageModelPipeline extends FilePersistentBase implements PageModelPipeline {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public FilePageModelPipeline() {
		setPath("./resource/temp/");
	}

	public FilePageModelPipeline(String path) {
		setPath(path);
	}

	@Override
	public void process(Object o, Task task) {
		String path = this.path + "/" + task.getUUID() + "/";
		try {
			String filename;
			if (o instanceof HasKey) {
				filename = path + ((HasKey) o).key() + ".html";
			} else {
				filename = path + DigestUtils.md5Hex(ToStringBuilder.reflectionToString(o)) + ".html";
			}
			PrintWriter printWriter = new PrintWriter(new FileWriter(getFile(filename)));
			printWriter.write(ToStringBuilder.reflectionToString(o));
			printWriter.close();
		} catch (IOException e) {
			logger.warn("write file error", e);
		}
	}
}
