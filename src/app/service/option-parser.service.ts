import { Injectable, OnInit } from '@angular/core';
import { Option } from '../model/option.model';

@Injectable({
  providedIn: 'root'
})
export class OptionParserService implements OnInit {

  //Change this to signals  
  private options:  Option[][][] = [];

  constructor() { }

  ngOnInit() {
    this.readAndParseFiles();
    this.areIdsUnique();
  }
  /*
  Read and parse the structure of folders / assets from Firebase and create the array.
  
  Options[][][]

    OptionLevel1  - OptionLevel2  - OptionLevel3
                                  - OptionLevel3
                                  - OptionLevel3

                  - OptionLevel2  - OptionLevel3

    OptionLevel1  - OptionLevel2
                  - OptionLevel2
                  - OptionLevel2

    OptionLevel1  - OptionLevel2

    OptionLevel1  - OptionLevel2
  */
  private readAndParseFiles() {
    // ##### THIS SHOULD BE READ FROM REMOTE #####
    const tempFileNames: string[] = [];
    tempFileNames.push("head_1___headMain.png");    
    tempFileNames.push("icon_head.png");
    tempFileNames.push("age_1___headMain.png");
    tempFileNames.push("icon_age.png");
    tempFileNames.push("eye_2_head__headMain.png");
    tempFileNames.push("icon_eye.png");
    tempFileNames.push("eye1_3_eye__headMain.png");
    tempFileNames.push("icon_eye1.png");
    tempFileNames.push("eye2_3_eye__headMain.png");
    tempFileNames.push("icon_eye2.png");
    // ###########################################

    for (let i = 0; i < tempFileNames.length; i++) {
        const id: string = "";
        const parentId: string = "";
        const level: number = 0;
        const texture: string = "";
        const icon: string = "";

        if (!tempFileNames[i].startsWith("icon")) {
          const option: Option = {
            id: id,
            parentId: parentId,
            level: level,
            texture: texture,
            icon: icon
          }
        }

        if (tempFileNames[i].startsWith("icon")) {
            //add icon to object.icon
        }
        

        // add object to "objects" array
    }

  }

  parseTextures(textures: string[]) {

  }

  private areIdsUnique() {
    //App should not start
  }

  private readId(fileName: string): string {
    return this.getSplit(fileName, 0);
  }

  private readLevel(level: string): string {
    return this.getSplit(level, 1);
  }

  private readParentId(parentId: string): string {
    return this.getSplit(parentId, 2);
  }

  private readPack(pack: string): string {
    return this.getSplit(pack, 3);
  }

  private readName(name: string): string {
    return this.getSplit(name, 4);
  }

  private getSplit(fileName: string, split: number): string{
    return fileName.split('_')[split];
  }


}
