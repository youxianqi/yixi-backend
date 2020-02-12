package youxianqi.yixi.utils;

        import java.awt.Color;
        import java.awt.Font;
        import java.awt.Graphics;
        import java.awt.Graphics2D;
        import java.awt.RenderingHints;
        import java.awt.geom.AffineTransform;
        import java.awt.image.BufferedImage;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.OutputStream;
        import java.util.Random;
        import javax.imageio.ImageIO;

public class CaptchaUtil {
    // 字体只显示大写，去掉了1,0,i,o几个容易混淆的字符
    private static final String VERIFY_CODES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static Random random = new Random();

    /**
     * 根据验证码长度生成验证码
     *
     * @param verifySize 验证码长度
     * @return 验证码
     */
    public static String generateVerifyCode(int verifySize) {
        return generateVerifyCode(verifySize, VERIFY_CODES);
    }

    /**
     * 根据验证码长度、指定验证码字符源生成验证码
     *
     * @param verifySize 验证码长度
     * @param sources    验证码字符源
     * @return 验证码
     */
    public static String generateVerifyCode(int verifySize, String sources) {
        if (sources == null || sources.length() == 0) {
            sources = VERIFY_CODES;
        }
        int codesLen = sources.length();
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder verifyCode = new StringBuilder(verifySize);
        for (int i = 0; i < verifySize; i++) {
            verifyCode.append(sources.charAt(rand.nextInt(codesLen - 1)));
        }
        return verifyCode.toString();
    }

    /**
     * 生成随机验证码文件,并返回验证码值
     *
     * @param width      验证码宽度
     * @param high       验证码高度
     * @param outputFile
     * @param verifySize 验证码长度
     * @return 验证码
     * @throws IOException
     */
    public static String outputVerifyImage(int width, int high, File outputFile, int verifySize) throws IOException {
        String verifyCode = generateVerifyCode(verifySize);
        outputImage(width, high, outputFile, verifyCode);
        return verifyCode;
    }

    /**
     * 输出随机验证码图片流,并返回验证码值
     *
     * @param width      验证码宽度
     * @param high       验证码高度
     * @param os
     * @param verifySize 验证码长度
     * @return 验证码
     * @throws IOException
     */
    public static String outputVerifyImage(int width, int high, OutputStream os, int verifySize) throws IOException {
        String verifyCode = generateVerifyCode(verifySize);
        outputImage(width, high, os, verifyCode);
        return verifyCode;
    }

    /**
     * 输出指定验证码图像文件
     *
     * @param width      验证码宽度
     * @param high       验证码高度
     * @param outputFile
     * @param code       验证码
     * @throws IOException
     */
    private static void outputImage(int width, int high, File outputFile, String code) throws IOException {
        if (outputFile == null) {
            return;
        }
        File dir = outputFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            outputFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(outputFile);
            outputImage(width, high, fos, code);
            fos.close();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 输出指定验证码图片流
     *
     * @param width 验证码宽度
     * @param high  验证码高度
     * @param os
     * @param code  验证码
     * @throws IOException
     */
    private static void outputImage(int width, int high, OutputStream os, String code) throws IOException {
        Random rand = new Random();

        int verifySize = code.length();

        BufferedImage image = new BufferedImage(width, high, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.GRAY);// 设置边框色
        g2.fillRect(0, 0, width, high);

        g2.setColor(Color.WHITE);// 设置背景色
        g2.fillRect(0, 2, width, high - 4);

        // 干扰线
        for (int i = 0; i < 4; i++) {
            Color color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
            g2.setColor(color);
            g2.drawLine(rand.nextInt(width), rand.nextInt(high), rand.nextInt(width), rand.nextInt(high));
        }

        // 使图片扭曲
        shear(g2, width, high, Color.WHITE);

        g2.setColor(getRandColor(100, 160));
        int fontSize = high - 4;
        Font font = new Font("arial", Font.ITALIC, fontSize);
        g2.setFont(font);
        char[] chars = code.toCharArray();
        for (int i = 0; i < verifySize; i++) {
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1), (width / verifySize) * i + fontSize / 2, high / 2);
            g2.setTransform(affine);
            g2.setColor(getRandColor(0, 200));
            g2.drawChars(chars, i, 1, ((width - 10) / verifySize) * i + 5, high / 2 + fontSize / 2 - 3);
        }
        g2.dispose();
        ImageIO.write(image, "jpg", os);
    }

    private static Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    private static int[] getRandomRgb() {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = random.nextInt(255);
        }
        return rgb;
    }

    private static void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private static void shearX(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(2);

        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }

    }

    private static void shearY(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(40) + 10; // 50;

        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }

        }

    }
}
