import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class GbkToUtf8 {
	private static final char BYTE_ORDER_MARK = '\uFEFF';

	/**
	 * batch change java source file charset : from GBK to UTF-8
	 * startingDir : sourceCode path
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Path startingDir = Paths.get("D:/xxx/src/main/java/com/");
		Files.walkFileTree(startingDir, new FindJavaVisitor());
	}

	private static class FindJavaVisitor extends SimpleFileVisitor<Path> {

		@Override
		public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) {
			try {
				if (filePath.toString().endsWith(".java")) {
					CharsetDecoder decoder = Charset.forName("GBK").newDecoder();
					CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();

					String srcFilePath =filePath.toFile().getPath();
					System.out.println(srcFilePath);
					File tmpFile = new File(srcFilePath+".tmp");
					try {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(new FileInputStream(filePath.toFile()), decoder));
						BufferedWriter bw = new BufferedWriter(
								new OutputStreamWriter(new FileOutputStream(tmpFile), encoder));
						char[] buffer = new char[1024];
						int read;
						//idea's javaFile does not with BOM
						//bw.write(BYTE_ORDER_MARK);
						try {
							while ((read = br.read(buffer)) != -1) {
								bw.write(buffer, 0, read);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}


						br.close();
						bw.close();
					} catch (Exception e) {
						e.printStackTrace();
					}	
					

					CopyOption[] options = new CopyOption[]{
						      StandardCopyOption.REPLACE_EXISTING
						    };
					
					String tmpFilePath = tmpFile.getPath();
					Files.delete(filePath);
					Files.move( FileSystems.getDefault().getPath(tmpFilePath), FileSystems.getDefault().getPath(srcFilePath), options);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return FileVisitResult.CONTINUE;
		}
	}
}
