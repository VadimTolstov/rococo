package guru.qa.rococo.utils;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


@ParametersAreNonnullByDefault
public class PngConverter {

  public static byte[] convert(String resourcePath) {
    validateInput(resourcePath);
    final File file = validateFile(resourcePath);

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      BufferedImage image = ImageIO.read(file);

      if (image == null) {
        throw new IllegalArgumentException("Не удалось прочитать изображение");
      }

      if (!ImageIO.write(image, "png", baos)) {
        throw new IllegalArgumentException("Ошибка конвертации в PNG");
      }

      return baos.toByteArray();

    } catch (IOException e) {
      throw new IllegalArgumentException("Ошибка обработки файла: " + e.getMessage());
    }
  }

  private static void validateInput(String path) {
    if (path == null || path.trim().isEmpty()) {
      throw new IllegalArgumentException("Путь не может быть пустым");
    }

    if (!path.toLowerCase().endsWith(".png")) {
      throw new IllegalArgumentException("Требуется файл с расширением .png");
    }
  }

  private static File validateFile(String path) {
    final File file = new File(path);

    if (!file.exists()) throw new IllegalArgumentException("Файл не существует");
    if (!file.isFile()) throw new IllegalArgumentException("Это директория, а не файл");
    if (!file.canRead()) throw new IllegalArgumentException("Нет прав на чтение");
    if (file.length() == 0) throw new IllegalArgumentException("Файл пустой");

    return file;
  }
}