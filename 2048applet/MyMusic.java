
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;


public class MyMusic {
	private AudioClip ac;
	private String path;
	private URL url = null;
	
	public MyMusic(String path) {
		this.path = path;
		initMusic();
	}

	public MyMusic(URL url) {
		this.url = url;
		System.out.println(url);
		initMusic1();
	}
	
	public void initMusic1(){
		ac = JApplet.newAudioClip(url);
	}
	
	public void initMusic() {
		File file = new File(path);
		URL url;
		try {
			url = file.toURI().toURL();
			ac = JApplet.newAudioClip(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	class MusicThread extends Thread {
		int mode;

		public MusicThread(int mode) {
			this.mode = mode;
		}

		@Override
		public void run() {
			try {
				if (mode == 1) {
					ac.play();
				} else if (mode == 2) {
					ac.loop();
				} else if (mode == 3) {
					ac.stop();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void startMusic() {
		new MusicThread(1).start();
	}

	public void loopMusic() {
		new MusicThread(2).start();
	}

	public void stopMusic() {
		new MusicThread(3).start();
	}
}
