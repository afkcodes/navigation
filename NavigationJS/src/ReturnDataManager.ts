﻿import ConverterFactory = require('./converter/ConverterFactory');
import NavigationSettings = require('./NavigationSettings');
import State = require('./config/State');

class ReturnDataManager {
    private static SEPARATOR = '_';
    //private static RET_1_SEP = '1_';
    private static RET_2_SEP = '2_';
    //private static RET_3_SEP = '3_';

    /*static formatReturnData(settings: NavigationSettings, converterFactory: ConverterFactory, state: State, returnData: any): string {
        var returnDataArray: string[] = [];
        for (var key in returnData) {
            if (returnData[key] != null && returnData[key].toString()) {
                var val = this.formatURLObject(settings, converterFactory, key, returnData[key], state, true).val;
                if (!settings.router.supportsDefaults || val !== state.formattedDefaults[key])
                    returnDataArray.push(this.encodeUrlValue(key) + this.RET_1_SEP + val);
            }
        }
        return returnDataArray.join(this.RET_3_SEP);
    }*/

    private static decodeUrlValue(urlValue: string): string {
        return urlValue.replace(new RegExp('0' + this.SEPARATOR, 'g'), this.SEPARATOR);
    }

    private static encodeUrlValue(urlValue: string): string {
        return urlValue.replace(new RegExp(this.SEPARATOR, 'g'), '0' + this.SEPARATOR);
    }

    static formatURLObject(settings: NavigationSettings, converterFactory: ConverterFactory, key: string, urlObject: any, state: State, encode = false): { val: string, arrayVal?: string[] } {
        encode = encode || state.trackTypes;
        var defaultType: string = state.defaultTypes[key] ? state.defaultTypes[key] : 'string';
        var converter = converterFactory.getConverter(urlObject);
        var convertedValue = converter.convertTo(urlObject, settings.combineArray);
        var formattedValue = convertedValue.val;
        var formattedArray = convertedValue.arrayVal;
        if (encode) {
            formattedValue = this.encodeUrlValue(formattedValue);
            if (formattedArray)
                formattedArray[0] = this.encodeUrlValue(formattedArray[0]);
        }
        if (state.trackTypes && converter.name !== defaultType) {
            formattedValue += this.RET_2_SEP + converter.key;
            if (formattedArray)
                formattedArray[0] = formattedArray[0] + this.RET_2_SEP + converter.key;
        }
        return { val: formattedValue, arrayVal: formattedArray };
    }

    static parseURLString(settings: NavigationSettings, converterFactory: ConverterFactory, key: string, val: string | string[], state: State, decode = false, separable = false): any {
        decode = decode || state.trackTypes;
        var defaultType: string = state.defaultTypes[key] ? state.defaultTypes[key] : 'string';
        var urlValue = typeof val === 'string' ? val : val[0];
        var converterKey = converterFactory.getConverterFromName(defaultType).key;
        if (state.trackTypes && key !== 'crumb' && urlValue.indexOf(this.RET_2_SEP) > -1) {
            var arr = urlValue.split(this.RET_2_SEP);
            urlValue = arr[0];
            converterKey = arr[1];
        }
        if (decode && key !== 'crumb')
            urlValue = this.decodeUrlValue(urlValue);
        if (typeof val === 'string')
            val =  urlValue;
        else
            val[0] = urlValue;
        return converterFactory.getConverterFromKey(converterKey).convertFrom(val, settings.combineArray, separable);
    }

    /*static parseReturnData(settings: NavigationSettings, converterFactory: ConverterFactory, returnData: string, state: State): any {
        var navigationData = {};
        var returnDataArray = returnData.split(this.RET_3_SEP);
        for (var i = 0; i < returnDataArray.length; i++) {
            var nameValuePair = returnDataArray[i].split(this.RET_1_SEP);
            navigationData[this.decodeUrlValue(nameValuePair[0])] = this.parseURLString(settings, converterFactory, this.decodeUrlValue(nameValuePair[0]), nameValuePair[1], state, true);
        }
        return navigationData;
    }*/
}
export = ReturnDataManager;