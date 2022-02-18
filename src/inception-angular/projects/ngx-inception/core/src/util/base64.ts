/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * The Base64 class.
 *
 * @author Marcus Portmann
 */
export class Base64 {

  static readonly CHARACTERS: string = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';

  static readonly LOOKUP: Uint8Array = new Uint8Array(256);

  static _Initialize() {
    for (let i = 0; i < Base64.CHARACTERS.length; i++) {
      Base64.LOOKUP[Base64.CHARACTERS.charCodeAt(i)] = i;
    }

    this._Initialize = () => {
    };
  }

  /**
   * Base64 decode the string data.
   *
   * @param base64 The base64 encoded data.
   *
   * @return The binary data.
   */
  public static decode(base64: string): ArrayBuffer {
    let bufferLength = base64.length * 0.75;
    const len = base64.length;
    let i;
    let p = 0;
    let encoded1;
    let encoded2;
    let encoded3;
    let encoded4;

    if (base64[base64.length - 1] === '=') {
      bufferLength--;
      if (base64[base64.length - 2] === '=') {
        bufferLength--;
      }
    }

    const arrayBuffer = new ArrayBuffer(bufferLength);
    const bytes = new Uint8Array(arrayBuffer);

    for (i = 0; i < len; i += 4) {
      encoded1 = Base64.LOOKUP[base64.charCodeAt(i)];
      encoded2 = Base64.LOOKUP[base64.charCodeAt(i + 1)];
      encoded3 = Base64.LOOKUP[base64.charCodeAt(i + 2)];
      encoded4 = Base64.LOOKUP[base64.charCodeAt(i + 3)];

      // eslint:disable-next-line:no-bitwise
      bytes[p++] = (encoded1 << 2) | (encoded2 >> 4);
      // eslint:disable-next-line:no-bitwise
      bytes[p++] = ((encoded2 & 15) << 4) | (encoded3 >> 2);
      // eslint:disable-next-line:no-bitwise
      bytes[p++] = ((encoded3 & 3) << 6) | (encoded4 & 63);
    }

    return arrayBuffer;
  }

  /**
   * Base64 encode the binary data.
   *
   * @param buffer The the binary data.
   *
   * @return The base64 encoded data.
   */
  public static encode(buffer: ArrayBuffer): string {
    const bytes = new Uint8Array(buffer);
    let i;
    const len = bytes.length;
    let base64 = '';

    for (i = 0; i < len; i += 3) {
      // eslint:disable-next-line:no-bitwise
      base64 += Base64.CHARACTERS[bytes[i] >> 2];
      // eslint:disable-next-line:no-bitwise
      base64 += Base64.CHARACTERS[((bytes[i] & 3) << 4) | (bytes[i + 1] >> 4)];
      // eslint:disable-next-line:no-bitwise
      base64 += Base64.CHARACTERS[((bytes[i + 1] & 15) << 2) | (bytes[i + 2] >> 6)];
      // eslint:disable-next-line:no-bitwise
      base64 += Base64.CHARACTERS[bytes[i + 2] & 63];
    }

    if ((len % 3) === 2) {
      base64 = base64.substring(0, base64.length - 1) + '=';
    } else if (len % 3 === 1) {
      base64 = base64.substring(0, base64.length - 2) + '==';
    }

    return base64;
  }
}

Base64._Initialize();
