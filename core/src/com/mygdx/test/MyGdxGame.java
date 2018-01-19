package com.mygdx.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Sprite img;
	ShaderProgram shaderProgram;
	private String frag;
	private String frag2;

	@Override
	public void create () {
		batch = new SpriteBatch();
		final String vert = "attribute vec4 a_position; //позиция вершины\n" +
				"attribute vec4 a_color; //цвет вершины\n" +
				"attribute vec2 a_texCoord0; //координаты текстуры\n" +
				"uniform mat4 u_projTrans;  //матрица, которая содержим данные для преобразования проекции и вида\n" +
				"varying vec4 v_color;  //цвет который будет передан в фрагментный шейдер\n" +
				"varying vec2 v_texCoords;  //координаты текстуры\n" +
				"void main(){\n" +
				"    v_color=a_color;\n" +
				"    // При передаче цвет из SpriteBatch в шейдер, происходит преобразование из ABGR int цвета в float. \n" +
				"    // что-бы избежать NAN  при преобразование, доступен не весь диапазон для альфы, а только значения от (0-254)\n" +
				"    //чтобы полностью передать непрозрачность цвета, когда альфа во float равна 1, то всю альфу приходится умножать.\n" +
				"    //это специфика libgdx и о ней надо помнить при переопределение  вершинного шейдера.\n" +
				"    v_color.a = v_color.a * (255.0/254.0);\n" +
				"    v_texCoords = a_texCoord0;\n" +
				"    //применяем преобразование вида и проекции, можно не забивать себе этим голову\n" +
				"    // тут происходят математические преобразование что-бы правильно учесть параметры камеры\n" +
				"    // gl_Position это окончательная позиция вершины \n" +
				"    gl_Position =  u_projTrans * a_position; \n" +
				"}";
		 frag  = "#ifdef GL_ES\n" +
				"    #define LOWP lowp\n" +
				"    precision mediump float;\n" +
				"#else\n" +
				"    #define LOWP\n" +
				"#endif\n" +
				"varying LOWP vec4 v_color;\n" +
				"varying vec2 v_texCoords;\n" +
				"uniform sampler2D u_texture;\n" +
				"void main(){\n" +
				"    //как и в стандартном шейдере получаем итоговый цвет пикселя\n" +
				"    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" +
				"    //после получения итогового цвета, меняем его на противоположный\n" +
				"    //gl_FragColor.rgb=1.0-gl_FragColor.rgb;\n" +
				"}";
		shaderProgram = new ShaderProgram(vert,frag);
		batch.setShader(shaderProgram);
		img = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg")));
		img.setPosition(0,0);
		img.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		Gdx.input.setInputProcessor(new InputProcessor() {
			@Override
			public boolean keyDown(int keycode) {
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				frag2  = "#ifdef GL_ES\n" +
						"    #define LOWP lowp\n" +
						"    precision mediump float;\n" +
						"#else\n" +
						"    #define LOWP\n" +
						"#endif\n" +
						"varying LOWP vec4 v_color;\n" +
						"varying vec2 v_texCoords;\n" +
						"uniform sampler2D u_texture;\n" +
						"void main(){\n" +
						"    //как и в стандартном шейдере получаем итоговый цвет пикселя\n" +
						"    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" +
						"    //после получения итогового цвета, меняем его на противоположный\n" +
						"    gl_FragColor.rgb=gl_FragColor.rgb; " +
						" 	 gl_FragColor.r = gl_FragColor.r*0.30;\n" +
						"	 gl_FragColor.g = gl_FragColor.g*0.00;\n" +
						"	 gl_FragColor.b = gl_FragColor.b*0.11;" +
						"\n" +
						"\n" +
						"}";
				batch.setShader(new ShaderProgram(vert,frag2));
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				Color color = new Color();
				batch.setShader(new ShaderProgram(vert,frag));


				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				img.setPosition(screenX-img.getWidth()/2,Gdx.graphics.getHeight() - screenY-img.getWidth()/2);
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {

				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				if (amount == -1) img.setScale(img.getScaleY() + 0.1f);
				if (amount == 1)  img.setScale(img.getScaleY() - 0.1f);
				return false;
			}
		});
	}


	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		img.draw(batch);
		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.getTexture().dispose();
	}
}
