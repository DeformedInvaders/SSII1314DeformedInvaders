package com.main.model;

import com.creation.data.TTipoSticker;
import com.game.data.TTipoEntidad;
import com.game.game.TTipoEndgame;
import com.game.select.TTipoLevel;
import com.video.data.TTipoActores;

public class GameResources
{	
	// Resources
	public static final String RESOURCE_DRAWABLE = "drawable";
	public static final String RESOURCE_ID = "id";
	
	// Drawable
	private static final String DRAWABLE_STICKER_ID = "sticker_";
	private static final String DRAWABLE_MISSILE_ID = "missile_";
	private static final String DRAWABLE_OBSTACLE_ID = "obstacle_";
	private static final String DRAWABLE_ENEMY_ID = "enemy_";
	private static final String DRAWABLE_BOSS_ID = "boss_";
	
	private static final String DRAWABLE_RATIO_LONG = "long_";
	private static final String DRAWABLE_RATIO_NOLONG = "notlong_";
		
	private static final String DRAWABLE_EYES_ID = "eyes_";
	private static final String DRAWABLE_MOUTH_ID = "mouth_";
	private static final String DRAWABLE_WEAPON_ID = "weapon_";
	private static final String DRAWABLE_TRINKET_ID = "trinket_";
	private static final String DRAWABLE_HELMET_ID = "helmet_";

	private static final String DRAWABLE_MOON_ID = "moon_";
	private static final String DRAWABLE_NEWYORK_ID = "newyork_";
	private static final String DRAWABLE_ROME_ID = "rome_";
	private static final String DRAWABLE_EGYPT_ID = "egypt_";
	private static final String DRAWABLE_STONEHENGE_ID = "stonehenge_";
	private static final String DRAWABLE_VIDEO_ID = "video_";
	
	private static final String DRAWABLE_GUITARRIST_ID = "guitarrist";
	private static final String DRAWABLE_SCIENTIFIC_ID = "scientific";
	
	private static final String DRAWABLE_BACKGROUND_ID = "background_";
	private static final String DRAWABLE_POLAROID_ID = "polaroid_";
	private static final String DRAWABLE_ACHIEVEMENT_ID = "achievement_";
	
	private static final String DRAWABLE_GAMEOVER_ID = "gameover";
	private static final String DRAWABLE_PERFECTED_ID = "levelperfected";
	private static final String DRAWABLE_COMPLETED_ID = "levelcompleted";
	private static final String DRAWABLE_MASTERED_ID = "levelmastered";
	
	// Enemies Files
	private static final String ENEMIES_PATH = "enemies/enemy_";
	private static final String BOSS_PATH = "enemies/boss_";
	public static final String ENEMIES_EXTENSION = ".edi";
	
	// Actors Files
	private static final String ACTORS_PATH = "actors/actor_";
	public static final String CHARACTER_EXTENSION = ".cdi";
	
	// Other Files
	public static final String EXTENSION_IMAGE_FILE = ".png";
	public static final String EXTENSION_AUDIO_FILE = ".3gp";
	public static final String EXTENSION_TEXT_FILE = ".txt";
	
	// Raw Video
	private static final String VIDEO_PATH = "android.resource://com.project.main/raw/";
	
	public static final String VIDEO_DESIGN_DRAW_PATH = VIDEO_PATH + "tips_design_draw";
	public static final String VIDEO_DESIGN_DRAG_PATH = VIDEO_PATH + "tips_design_drag";
	public static final String VIDEO_DESIGN_ROTATE_PATH = VIDEO_PATH + "tips_design_rotate";
	public static final String VIDEO_DESIGN_ZOOM_PATH = VIDEO_PATH + "tips_design_zoom";
	public static final String VIDEO_DESIGN_OUTSIDE_PATH = VIDEO_PATH + "tips_design_outside";
	public static final String VIDEO_DESIGN_NOREGULAR_PATH = VIDEO_PATH + "tips_design_noregular";
	
	public static final String VIDEO_PAINT_PENCIL_PATH = VIDEO_PATH + "tips_paint_pencil";
	public static final String VIDEO_PAINT_BUCKET_PATH = VIDEO_PATH + "tips_paint_bucket";
	public static final String VIDEO_PAINT_STICKER_PATH = VIDEO_PATH + "tips_paint_sticker";
	public static final String VIDEO_PAINT_EDITSTICKER_PATH = VIDEO_PATH + "tips_paint_editsticker";
	public static final String VIDEO_PAINT_ZOOM_PATH = VIDEO_PATH + "tips_paint_zoom";
	
	public static final String VIDEO_DEFORM_HANDLES_PATH = VIDEO_PATH + "tips_deform_handles";
	public static final String VIDEO_DEFORM_UNDEFINED_PATH = VIDEO_PATH + "tips_deform_undefined";
	
	public static final String VIDEO_GAME_LIVES_PATH = VIDEO_PATH + "tips_game_lives";
	public static final String VIDEO_GAME_CROUCH_PATH = VIDEO_PATH + "tips_game_crouch";
	public static final String VIDEO_GAME_JUMP_PATH = VIDEO_PATH + "tips_game_jump";
	public static final String VIDEO_GAME_ATTACK_PATH = VIDEO_PATH + "tips_game_attack";
	public static final String VIDEO_GAME_COMPLETE_PATH = VIDEO_PATH + "tips_game_complete";
	
	// Fonts
	private static final String FONT_PATH = "fonts/font_";
	private static final String FONT_EXTENSION = ".ttf";
	
	public static final String FONT_TYPEWRITER_PATH = FONT_PATH + "typewriter" + FONT_EXTENSION;
	public static final String FONT_LOGO_PATH = FONT_PATH + "logo" + FONT_EXTENSION;
	
	private static final String FONT_MOON_PATH = FONT_PATH + "moon" + FONT_EXTENSION;
	private static final String FONT_NEWYORK_PATH = FONT_PATH + "new_york" + FONT_EXTENSION;
	private static final String FONT_ROME_PATH = FONT_PATH + "rome" + FONT_EXTENSION;
	private static final String FONT_EGYPT_PATH = FONT_PATH + "egypt" + FONT_EXTENSION;
	private static final String FONT_STONEHENGE_PATH = FONT_PATH + "stonehenge" + FONT_EXTENSION;
	
	public static final String GET_ENEMIES(TTipoEntidad entidad, TTipoLevel nivel, int pos)
	{
		String tipoEntidad;
		
		switch(entidad)
		{
			case Enemigo:
				tipoEntidad = DRAWABLE_ENEMY_ID;
			break;
			case Obstaculo:
				tipoEntidad = DRAWABLE_OBSTACLE_ID;
			break;
			case Misil:
				tipoEntidad = DRAWABLE_MISSILE_ID;
			break;
			case Jefe:
				tipoEntidad = DRAWABLE_BOSS_ID;
			break;
			default:
				return null;
		}
		
		switch(nivel)
		{
			case Moon:
				return tipoEntidad + DRAWABLE_MOON_ID + (pos + 1);
			case NewYork:
				return tipoEntidad + DRAWABLE_NEWYORK_ID + (pos + 1);
			case Rome:
				return tipoEntidad + DRAWABLE_ROME_ID + (pos + 1);
			case Egypt:
				return tipoEntidad + DRAWABLE_EGYPT_ID + (pos + 1);
			case Stonehenge:
				return tipoEntidad + DRAWABLE_STONEHENGE_ID + (pos + 1);
			default:
				return null;
		}
	}	
	
	public static final String GET_ENEMIES_FILES(TTipoLevel nivel, int pos)
	{
		switch(nivel)
		{
			case Moon:
				return ENEMIES_PATH + DRAWABLE_MOON_ID + (pos + 1) + ENEMIES_EXTENSION;
			case NewYork:
				return ENEMIES_PATH + DRAWABLE_NEWYORK_ID + (pos + 1) + ENEMIES_EXTENSION;
			case Rome:
				return ENEMIES_PATH + DRAWABLE_ROME_ID + (pos + 1) + ENEMIES_EXTENSION;
			case Egypt:
				return ENEMIES_PATH + DRAWABLE_EGYPT_ID + (pos + 1) + ENEMIES_EXTENSION;
			case Stonehenge:
				return ENEMIES_PATH + DRAWABLE_STONEHENGE_ID + (pos + 1) + ENEMIES_EXTENSION;
			default:
				return null;
		}
	}
	
	public static final String GET_BOSS_FILES(TTipoLevel nivel, int pos)
	{
		switch(nivel)
		{
			case Moon:
				return BOSS_PATH + DRAWABLE_MOON_ID + (pos + 1) + ENEMIES_EXTENSION;
			case NewYork:
				return BOSS_PATH + DRAWABLE_NEWYORK_ID + (pos + 1) + ENEMIES_EXTENSION;
			case Rome:
				return BOSS_PATH + DRAWABLE_ROME_ID + (pos + 1) + ENEMIES_EXTENSION;
			case Egypt:
				return BOSS_PATH + DRAWABLE_EGYPT_ID + (pos + 1) + ENEMIES_EXTENSION;
			case Stonehenge:
				return BOSS_PATH + DRAWABLE_STONEHENGE_ID + (pos + 1) + ENEMIES_EXTENSION;
			default:
				return null;
		}
	}
	
	public static final String GET_ACTORS_FILES(TTipoActores actor)
	{
		switch(actor)
		{
			case Guitarrista:
				return ACTORS_PATH + DRAWABLE_GUITARRIST_ID + CHARACTER_EXTENSION;
			case Cientifico:
				return ACTORS_PATH + DRAWABLE_SCIENTIFIC_ID + CHARACTER_EXTENSION;
			default:
				return null;
		}
	}
	
	public static final String GET_STICKER(TTipoSticker pegatina, int pos)
	{
		switch(pegatina)
		{
			case Trinket:
				return DRAWABLE_STICKER_ID + DRAWABLE_TRINKET_ID + (pos + 1);
			case Helmet:
				return DRAWABLE_STICKER_ID + DRAWABLE_HELMET_ID + (pos + 1);
			case Eyes:
				return DRAWABLE_STICKER_ID + DRAWABLE_EYES_ID + (pos + 1);
			case Mouth:
				return DRAWABLE_STICKER_ID + DRAWABLE_MOUTH_ID + (pos + 1);
			case Weapon:
				return DRAWABLE_STICKER_ID + DRAWABLE_WEAPON_ID + (pos + 1);
			default:
				return null;
		}
	}
	
	public static final String GET_BACKGROUND(TTipoLevel nivel, int pos)
	{
		String ratio;
		
		if (GamePreferences.IS_LONG_RATIO())
		{
			ratio = DRAWABLE_RATIO_LONG;
		}
		else
		{
			ratio = DRAWABLE_RATIO_NOLONG;
		}
		
		switch(nivel)
		{
			case Moon:
				return DRAWABLE_BACKGROUND_ID + ratio + DRAWABLE_MOON_ID + (pos + 1);
			case NewYork:
				return DRAWABLE_BACKGROUND_ID + ratio + DRAWABLE_NEWYORK_ID + (pos + 1);
			case Rome:
				return DRAWABLE_BACKGROUND_ID + ratio + DRAWABLE_ROME_ID + (pos + 1);
			case Egypt:
				return DRAWABLE_BACKGROUND_ID + ratio + DRAWABLE_EGYPT_ID + (pos + 1);
			case Stonehenge:
				return DRAWABLE_BACKGROUND_ID + ratio + DRAWABLE_STONEHENGE_ID + (pos + 1);
			default:
				return null;
		}
	}
	
	public static final String GET_VIDEO(int pos)
	{
		String ratio;
		
		if (GamePreferences.IS_LONG_RATIO())
		{
			ratio = DRAWABLE_RATIO_LONG;
		}
		else
		{
			ratio = DRAWABLE_RATIO_NOLONG;
		}
		
		return DRAWABLE_BACKGROUND_ID + ratio + DRAWABLE_VIDEO_ID + (pos + 1);
	}
	
	public static final String GET_POLAROID(TTipoLevel nivel, TTipoEndgame juego)
	{
		String tipoJuego;
		
		switch(juego)
		{
			case GameOver:
				tipoJuego = DRAWABLE_GAMEOVER_ID;
				break;
			case LevelCompleted:
				tipoJuego = DRAWABLE_COMPLETED_ID;
				break;
			case LevelPerfected:
				tipoJuego = DRAWABLE_PERFECTED_ID;
				break;
			case LevelMastered:
				tipoJuego = DRAWABLE_MASTERED_ID;
				break;
			default:
				return null;
		}
		
		switch(nivel)
		{
			case Moon:
				return DRAWABLE_POLAROID_ID + DRAWABLE_MOON_ID + tipoJuego;
			case NewYork:
				return DRAWABLE_POLAROID_ID + DRAWABLE_NEWYORK_ID + tipoJuego;
			case Rome:
				return DRAWABLE_POLAROID_ID + DRAWABLE_ROME_ID + tipoJuego;
			case Egypt:
				return DRAWABLE_POLAROID_ID + DRAWABLE_EGYPT_ID + tipoJuego;
			case Stonehenge:
				return DRAWABLE_POLAROID_ID + DRAWABLE_STONEHENGE_ID + tipoJuego;
			default:
				return null;
		}		
	}
	
	public static final String GET_ACHIEVEMENTS(TTipoLevel nivel, TTipoEndgame juego)
	{
		String tipoJuego;
		
		switch(juego)
		{
			case LevelCompleted:
				tipoJuego = DRAWABLE_COMPLETED_ID;
				break;
			case LevelPerfected:
				tipoJuego = DRAWABLE_PERFECTED_ID;
				break;
			case LevelMastered:
				tipoJuego = DRAWABLE_MASTERED_ID;
				break;
			default:
				return null;
		}
		
		switch(nivel)
		{
			case Moon:
				return DRAWABLE_ACHIEVEMENT_ID + DRAWABLE_MOON_ID + tipoJuego;
			case NewYork:
				return DRAWABLE_ACHIEVEMENT_ID + DRAWABLE_NEWYORK_ID + tipoJuego;
			case Rome:
				return DRAWABLE_ACHIEVEMENT_ID + DRAWABLE_ROME_ID + tipoJuego;
			case Egypt:
				return DRAWABLE_ACHIEVEMENT_ID + DRAWABLE_EGYPT_ID + tipoJuego;
			case Stonehenge:
				return DRAWABLE_ACHIEVEMENT_ID + DRAWABLE_STONEHENGE_ID + tipoJuego;
			default:
				return null;
		}		
	}
	
	public static final String GET_FONT_PATH(TTipoLevel nivel)
	{
		switch(nivel)
		{
			case Moon:
				return FONT_MOON_PATH;
			case NewYork:
				return FONT_NEWYORK_PATH;
			case Rome:
				return FONT_ROME_PATH;
			case Egypt:
				return FONT_EGYPT_PATH;
			case Stonehenge:
				return FONT_STONEHENGE_PATH;
			default:
				return null;
		}		
	}
	
}
